#
# -*- coding: utf-8 -*-

"""
2019-06-17 辻創太
mksummary-kita-d.pyで作成したsummary.htmlを
mksummary.pdfで示されているようにWeb上で動かせるよう修正しました。
修正箇所は以下の通りです。
・タイムスタンプの形式の不一致の修正
・作成されたフォルダ名と正規表現の不一致の修正(2箇所)
・5/21以前のフォーマットの削除
"""

import pathlib
import pytz
import datetime
import urllib
import re
import io
import sys
import argparse
import os
import shutil


def foreachpersonaldir(d, serial, name, root=pathlib.Path('.')):
    """個人フォルダの中をパースし, 必要な情報を取り出す

    Args:
        d (pathlib.Path): folder name to parse
        root (pathlib.Path): root directory

    Returns:
        dictionary of folder contents,
        {'dirname': str()   # name of folder, relative to root
         'timestamp': None, # content of timestamp.txt
         'submissionText': None,      # content of (dirname)_submissionText.html
         'attachments': []  # attached files; list of pathlib.Path() relative to root
         }
    """

    obj = {'dirname': str(d.relative_to(root)),
           'timestamp': None,
           'submissionText': None,
           'attachments': []}

    tpath = d / 'timestamp.txt'
    if tpath.exists():
        tstext = tpath.open('r').read()  # for > py3.5 tpath.read_text() is smart
        tstamp = datetime.datetime.strptime(tstext[:20], '%Y-%m-%dT%H:%M:%SZ')
        # loaded timestamp is in utc, translate to jst
        # see http://nekoya.github.io/blog/2013/06/21/python-datetime/
        tstamp = pytz.utc.localize(tstamp)  # attatch tzinfo as utc
        tstamp = tstamp.astimezone(pytz.timezone('Asia/Tokyo'))  # apply JST
        obj['timestamp'] = tstamp

        # html テキスト d.name + '_submissionText.html'
        spath = d / (d.name + '_submissionText.html')
        if spath.exists():
            # BOM付きutf8
            obj['submissionText'] = spath.open('r', encoding='utf-8-sig').read()

        # show submitted files
        attachment_dir = d / '提出物の添付'
        #
        # 添付ファイルにシリアルナンバーをつけて課題全体でユニークにする
        #
        for f in attachment_dir.glob('*'):
        #    distfilename = str(serial) + "_" + f.name
            distfilename = str(serial) + "_" + name + "_" + f.name
            distname = f.parent.as_posix()+"/"+ distfilename
            os.rename(f.as_posix(), distname)
        #    print("rename "+ f.relative_to(root).as_posix()+", "+str(serial)+"_"+f.name)
        #
        # 添付ファイル集積フォルダにコピー
        #
            asname = assignmentspath.as_posix() + "/" + distfilename
            shutil.copy(distname, asname)
        # 名前変更後の添付ファイルを登録
        for f in attachment_dir.glob('*'):
            obj['attachments'].append(f.relative_to(root))
        # for English mode
        attachment_dir = d / 'Submission attachment(s)'
        #
        # 添付ファイルにシリアルナンバーをつけて課題全体でユニークにする
        #
        for f in attachment_dir.glob('*'):
            distfilename = str(serial) + "_" + f.name
            distname = f.parent.as_posix()+"/"+ distfilename
            os.rename(f.as_posix(), distname)
        #    print("rename "+ f.relative_to(root).as_posix()+", "+str(serial)+"_"+f.name)
        #
        # 添付ファイル集積フォルダにコピー
        #
            asname = assignmentspath.as_posix() + "/" + distfilename
            shutil.copy(distname, asname)
        # 名前変更後の添付ファイルを登録
        for f in attachment_dir.glob('*'):
            obj['attachments'].append(f.relative_to(root))

    return obj


def walk_personal_dirs(root=pathlib.Path('.')):
    """パス上のフォルダを検索し, foreachpersondir のコンテンツをiterativeに取得する.

    Args:
        root (pathlib.Path) 検索開始パス

    Yields:
        {
            id (str): 学生番号
            name (str): 氏名
            personaldir: None or (result from foreachpersonaldir)
        }
    """

    # 5/21 以降のフォーマット
    #
    # 添付ファイル名をユニークにするためのシリアルナンバー
    serial = 1
    dirs = root.glob('*(*)')
    for d in dirs:
        # '氏名 (ID)' の形式に合致したフォルダだけを採用. 
        if d.is_dir():
            reobj = re.compile('(?P<name>.+)\((?P<id>[0-9x]+)\)')
            mobj = reobj.match(d.name)
            if mobj:
                #
                # 氏名フォルダを採点結果集積フォルダにつくる 
                #
                gdir = gradingpath / d.name
                if not gdir.exists():
                    gdir.mkdir()
                # フォルダの中をパースする.
                yield {'id': mobj.group('id'),
                       'name': mobj.group('name'),
                       'personaldir': foreachpersonaldir(d, serial, mobj.group('name'), root)}
        serial = serial+1

def render_personalfolder(p,
                          writer,
                          enable_viewerjs=False,
                          scorefield=None,
                          delayfield=None,
                          commentfield=None):
    """ foreachpersonaldir の内容を html で出力する.
    Args:
        p (dict): foreachpersonaldir の結果. None の場合, 'フォルダが確認できません' を出力
        writer (File): htmlの出力対象
        enable_viwerjs (optional[bool]): ViewerJS でのプレビューに対応する.
        scorefield (optional[dict]): 得点フォームの情報, Noneの場合不要
            score (int): 現在の点数, None だとデフォルト値を採用
            fullscore (int): 満点
            formname (str): このフォームの name 属性
        delayfield (optional[dict]): 遅延フォームの情報, Noneの場合不要
            formname (str): このフォームの name 属性
        commentfield (optional[dict]): コメントフォームの情報, Noneの場合不要
            formname (str): このフォームの name 属性
            [data (str)]: テキストデータ
    """
    def formatscoreform(s, defaultscore):
        '''点数フォームをフォーマットする

        Args:
            s (dict): 得点フォームの情報
                score (int): 現在の点数, None だとデフォルト値を採用
                fullscore (int): 満点
                formname (str): このフォームの name 属性
            defaultscore (int): s['score'] == None の場合に設定する値
        '''

        f = '点数(score): <input type="text" value="{0:d}" name="{2:s}"> (0-{1:d})'
        return f.format(s['score'] if s['score'] is not None else defaultscore,
                        s['fullscore'],
                        s['formname'])

    def formatdelayform(d):
        '''点数フォームをフォーマットする

        Args:
            d (dict): 遅延フォームの情報
                formname (str): このフォームの name 属性
        '''

        f = ' 遅延(delay): <input type="checkbox" value="1" name="{0:s}"><br>'
        return f.format(d['formname'])

    def formatcommentform(c):
        '''コメントフォームをフォーマットする

        Args:
            c (dict): コメントフォームの情報
                formname (str): このフォームの name 属性
                [data (str)]: テキストデータ
        '''
        return '<textarea name="{0:s}" rows="3" cols="40">{1:s}</textarea><br>'.format(commentfield['formname'], commentfield.get('data', '確認しました。'))

    def printforms(writer, scorefield=None, defaultscore=0, delayfield=None, commentfield=None):
        '''点数フォーム, コメントフォームをフォーマット,  出力する

        Args:
            writer (File): htmlの出力対象
            scorefield (optional[dict]): 得点フォームの情報, Noneの場合不要
                score (int): 現在の点数, None だとデフォルト値を採用
                fullscore (int): 満点
                formname (str): このフォームの name 属性
            defaultscore: scorefield['score'] == None の場合に適用する点数
            commentfield (optional[dict]): コメントフォームの情報, Noneの場合不要
                formname (str): このフォームの name 属性
                [data (str)]: テキストデータ
        '''
        if scorefield:
            print(formatscoreform(scorefield, defaultscore), file=writer)
        if delayfield:
            print(formatdelayform(delayfield), file=writer)
        if commentfield:
            print('コメント(comment):<br>', file=writer)
            print(formatcommentform(commentfield), file=writer)

    if p is None:
        # デフォルトの成績を0
        printforms(writer, scorefield, 0, delayfield, commentfield)
        print('フォルダがありません(personal folder is not found)<br>', file=writer)
        return

    # タイムスタンプでコンテンツを確認
    if p['timestamp'] is None:
        # デフォルトの成績を0
        printforms(writer, scorefield, 0, delayfield, commentfield)
        print('提出未確認(materials not found)<br>', file=writer)
        return

    # 有効なコンテンツ
    # デフォルトの成績を100
    printforms(writer, scorefield, 100, delayfield, commentfield)
    # タイムスタンプ
    print('timestamp: {0:s}<br>'.format(str(p['timestamp'])), file=writer)
    # HTML
    if p['submissionText']:
        print('submissionText:<br>', file=writer)
        print('<div class="submissionText">', file=writer)
        print(p['submissionText'], file=writer)
        print('</div>', file=writer)
    # 添付ファイル
    if p['attachments']:
        print('attachments:<br>', file=writer)
        print('<div class="attachment">', file=writer)
        for a in p['attachments']:
            # リンクパスをurl形式に変換
            relurl = urllib.parse.urlunsplit(('', '', str(a.as_posix()), '', ''))
            # in sake for working on IE11 and Edge (and other browsers)
            # do not escape multibyte URL
            # linkurl = urllib.parse.quote(relurl)
            linkurl = relurl
            print('<a href="{0:s}" TARGET="_blank">'.format(linkurl), end='', file=writer)
            suffix = a.suffix.lower()
            if suffix in ('.png', '.jpg', '.jpeg', '.bmp'):
                print(relurl, '<br/>', sep='', end='', file=writer)
                # ビットマップならば埋め込み
                print('<img class="attachedimg" src="{0:s}">'.format(linkurl),
                      end='', file=writer)
                print('</a><br/>', file=writer)
            elif enable_viewerjs and (suffix in ('.pdf', '.odf')):
                #  ViewerJS によるプレビュー画面埋め込み
                print(relurl, '</a><br/>', file=writer)
                print('<iframe class="attacheddoc" src="_summary/ViewerJS/#../../{0:s}"'
                      'allowfullscreen webkitallowfullscreen></iframe>'.format(linkurl),
                      end='', file=writer)
            else:
                print('{0:s}</a><br/>'.format(linkurl), file=writer)
        print('</div>', file=writer)


def scoresheetscript(personal_dirs, writer):
    """得点表を生成するjavascriptを作成する

    Args:
        personal_dirs (list): walk_personal_dirs_idlist が返すオブジェクトのリスト
        writer (stream): 出力するファイル
    """

    print('''
<script type="text/javascript">
function makeScoreWindow() {
var page= window.open();
page.document.open();
page.document.write("<html>");''', file=writer)
    print('page.document.write("<head><title>点数表(score sheet): {0:s}</title></head>");'.format(assignmentname), file=writer)
    print('page.document.write("<body>");', file=writer)
    print('page.document.write("<H1>点数表(score sheet): {0:s}</H1>");'''.format(assignmentname), file=writer)
    print('''
page.document.write("表はコピー&amp;ペーストで表計算ソフトなどに貼り付けてご利用ください.<br>");
page.document.write("(Use this table on your spread sheet software with copy &amp; paste.)<hr>");
page.document.write("<table border>");
page.document.write("<tr><th>ID</th><th>氏名(Name)</th><th>点数(score)</th><th>遅延(delay)</th><th>フォルダ(folder)</th><th>コメント(comments)</th></tr>");
''', file=writer)

    #
    # 履修者の個々の表，form の値を参照してつくる
    #
    for p in personal_dirs:
        # フォルダ名を表示
        print('page.document.write("<tr><td>ID {0:s}</td>")'.format(p['id']), file=writer)
        print('page.document.write("<td>{0:s}</td>")'.format(p['name']), file=writer)
        print('page.document.write("<td>",document.form2.{0:s}.value,"</td>")'.format('s'+p['id']),file=writer)
        print('page.document.write("<td>",document.form2.{0:s}.checked,"</td>")'.format('d'+p['id']),file=writer)
        print('page.document.write("<td>{0:s}</td>")'.format(p['personaldir']['dirname']), file=writer)
        print('page.document.write("<td>",document.form2.{0:s}.value,"</td></tr>")'.format('c'+p['id']),file=writer)

    print('''
page.document.write("</body></html>");
page.document.close();
}
</script>''', file=writer)

def csvdownloadscript(personal_dirs, writer):
    """得点表をダウンロードするjavascriptを作成する

    Args:
        personal_dirs (list): walk_personal_dirs_idlist が返すオブジェクトのリスト
        writer (stream): 出力するファイル
    """
    print('''
<script type="text/javascript">
function handleDownload() {
    var bom = new Uint8Array([0xEF, 0xBB, 0xBF]);
    var content = '点数表(score sheet): 第２回\\n'
    + 'ID, 氏名(Name), 点数(score), 遅延(delay), フォルダ(folder), コメント(comments)\\n'
''', file=writer)
    for p in personal_dirs:

        print('    +\'ID {0:s},'.format(p['id']), end="", file=writer)
        print('{0:s},\''.format(p['name']), end="", file=writer)
        print('+ document.form2.{0:s}.value +\',\''.format('s'+p['id']), end="", file=writer)
        print('+ document.form2.{0:s}.checked+\','.format('d'+p['id']), end="", file=writer)
        print('{0:s},"\''.format(p['personaldir']['dirname']), end="", file=writer)
        print('+document.form2.{0:s}.value.replace(/"/g,"\\"\\"")+\'"\\n\''.format('c'+p['id']), file=writer)
    print('    +"";',file=writer)
    print('''
                var blob = new Blob([ bom, content ], { "type" : "text/csv" });

                if (window.navigator.msSaveOrOpenBlob) { 
                    // window.navigator.msSaveBlob(blob, "scores.csv"); 
                    // msSaveOrOpenBlobの場合はファイルを保存せずに開ける
                    window.navigator.msSaveOrOpenBlob(blob, "scores.csv"); 
                } else {
                    document.getElementById("download").href = window.URL.createObjectURL(blob);
                }
            }

</script>''', file=writer)



def main(output_buffer,
         root=pathlib.Path('.'),
         assignmentname='',
         html_output_encoding='utf-8',
         enable_viewerjs='False'):
    """
    root フォルダを巡回し, summary.html 等を出力する.

    Args:
        output_buffer (File): binary IO to output
        root (pathlib.Path): root path to walk
        assignmentname (str): title of HTML page
        html_output_encoding (str): encoding for output html
        enable_viwerjs (bool): ViewerJS でのプレビューに対応する.
    """

    # grades.xls を grading フォルダにコピー
    gradesfile = root.as_posix() + "/grades.xls"
    agradefile = gradingpath.as_posix() + "/grades.xls"
    shutil.copy(gradesfile,agradefile)

    writer = io.TextIOWrapper(output_buffer, encoding=html_output_encoding, newline='\n')

    # フォルダを巡回し, コンテンツのリストを作る.
    personal_dirs = list(walk_personal_dirs(root))

    # HTML の出力
    print('<!DOCTYPE html>', file=writer)
    print('<html>', file=writer)
    print('<head>', file=writer)
    print('  <meta charset="{0:s}">'.format(html_output_encoding), file=writer)
    print('  <title>{0:s}</title>'.format(assignmentname), file=writer)
    print('  <style type="text/css">', file=writer)
    print('''
div.submissionText {
  background: #f0f0f0;
  border: medium solid #0f0f0f;
  font-size: medium;
  margin: 0 0 0 10px;
  padding: 5px 10px 5px 10px;
}''', file=writer)
    print('''
img.attachedimg {
  width: 510px;
}
iframe.attacheddoc {
  width: 510px;
  height: 720px;
}
div.attachment {
  margin: 0 0 0 10px;
}''', file=writer)
    print('  </style>', file=writer)
    # 得点表生成
    scoresheetscript(personal_dirs, writer)
    csvdownloadscript(personal_dirs, writer)
    print('</head>', file=writer)
    print('<body>', file=writer)

    print('<H1>{0:s}</H1>'.format(assignmentname), file=writer)

    print('''
<form>
記入した点数で別 window に点数表を作る<br>
(Make a score sheet on another window)<br>
<input type="button" value="点数表表示(show score sheet)" onClick="makeScoreWindow()">
</form>
<a id="download" href="#" download="scores.csv" onclick="handleDownload()">CSV 形式でダウンロード(download scores as a CSV format file)</a>
''', file=writer)

    print('<form name="form2">', file=writer)

    for p in personal_dirs:
        # フォルダ名を表示
        print('<hr><h3>{0:s}</h3>'.format(p['personaldir']['dirname']), file=writer)
        
        # 採点用フォームデータ
        scorefield = {
            'formname': 's' + p['id'],
            'score': None,
            'fullscore': 100}
        delayfield = {
            'formname': 'd' + p['id'],
            'value': 0}
        commentfield = {
            'formname': 'c' + p['id']}
        render_personalfolder(p['personaldir'], writer,
                              enable_viewerjs=enable_viewerjs,
                              scorefield=scorefield,delayfield=delayfield,
                              commentfield=commentfield)

    print('</form></body></html>', file=writer)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument('root', nargs='?', type=str, default='.',
                        help='root directory or file path under root (default: %(default)s).'
                             'If file path is given, directory part is used as ROOT')
    parser.add_argument('--output', type=str, default='summary.html',
                        help='default output filename (default: %(default)s).'
                             'file is output as "ROOT/OUTPUT"')
    parser.add_argument('--viewerjs', default=False,
                        action='store_true',
                        help='enable ViewerJS PDF viewer')

    args = parser.parse_args()

    rootpath = pathlib.Path(args.root)
    if rootpath.is_file():
        rootpath = rootpath.parent

    outputpath = rootpath / args.output
#
# 開始バナー
#
    print("mksummary-kita-d, ver. 2018-04-06")
    print("作業フォルダ: ", rootpath)
    print("作成ファイル: ", outputpath)

#
# XLS 形式の採点簿ファイルの有無を確認
#
    gradesfilepath = rootpath / "grades.xls"
    if not gradesfilepath.exists():
        print("このフォルダに grades.xls がありません")
        dmy = input("終了します")
        sys.exit()
    if not gradesfilepath.is_file():
        print("このフォルダの grades.xls がファイルではありません")
        dmy = input("終了します")
        sys.exit()

#
#   添付ファイルを集積するフォルダのパスと
#   採点結果を集積するフォルダのパスをつくる
#
    global assignmentspath
    assignmentspath = rootpath / "assignments"
    global gradingpath
    gradingpath = rootpath / "grading"
#
#   添付ファイル集積フォルダと採点結果集積フォルダをつくる
#
    if not assignmentspath.exists():
        assignmentspath.mkdir()
    if not gradingpath.exists():
        gradingpath.mkdir()

    # rootpathの名前をタイトルにする
    assignmentname = rootpath.absolute().name

    with outputpath.open('wb') as output_buffer:
        main(output_buffer, root=rootpath, assignmentname=assignmentname,
             enable_viewerjs=args.viewerjs)
    dmy = input("終了します")
