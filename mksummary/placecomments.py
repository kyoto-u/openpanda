#
# -*- coding: utf-8 -*-

"""
2019-06-21 辻創太
Placecomments-enc.pyにscores.csvを渡した際に、
仕様通りgradingの下の当該学生のフォルダに
comments.txtを書き出すよう修正しました。
summary.htmlと同じフォルダ内にscore.csvを置いてください。
修正箇所は以下の通りです。
・foldernameおよびcommentsの参照位置の修正
"""

import pathlib
import pytz
import datetime
import urllib
import re
import io
import sys
import argparse
import csv

#
#
# 総当たり式での漢字コードの検索
#
def getEncode(filepath):
    encs = "iso-2022-jp euc-jp sjis utf-8-sig utf-8".split()
    for enc in encs:
        with filepath.open(encoding=enc) as fr:
            try:
                fr = fr.read()
            except UnicodeDecodeError:
                continue
        return enc
#
# scores.csv を読み込み grading の下の個人フォルダに comment.txt を作る
#
def loadscorescsv():
    scorecsvpath = rootpath / "scores.csv"
    if scorecsvpath.exists():
        enc = getEncode(scorecsvpath)
        csvfile = scorecsvpath.open('r', encoding=enc)
        i = 1
        print('encoding = '+enc) 
        for row in csv.reader(csvfile):
            if i<=2:
                 # ヘッダ行
                 print('header ',end="")
                 print(row)
            else:
                 print('data ', end="")
                 print(row)
                 foldername = row[5] + "," + row[6]
                 comments = row[7]
                 folderpath = gradingpath / foldername
                 print(folderpath)
                 if folderpath.exists():
                     if folderpath.is_dir():
                         commentspath = folderpath / 'comments.txt'
                         commentsfile = commentspath.open('w', encoding="utf-8")
                         commentsfile.write(comments)
                         commentsfile.close()
                         print('comment.txt を作成しました')
                     else:
                         print('folder is not directory')
                 else:
                     print('no folder')
            i = i+1 
    else:
        print('Unable to open scores.csv')

#
# プログラムのエントリーポイント
#
if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument('root', nargs='?', type=str, default='.',
                        help='root directory or file path under root (default: %(default)s).'
                             'If file path is given, directory part is used as ROOT')
#   parser.add_argument('--output', type=str, default='summary.html',
#                       help='default output filename (default: %(default)s).'
#                            'file is output as "ROOT/OUTPUT"')
#   parser.add_argument('--viewerjs', default=False,
#                       action='store_true',
#                       help='enable ViewerJS PDF viewer')

    args = parser.parse_args()

    rootpath = pathlib.Path(args.root)
    if rootpath.is_file():
        rootpath = rootpath.parent
#
# 開始バナー
#
    print("placements-enc-tsuji-modified, ver. 2019-06-21")
    print("作業フォルダ: ", rootpath)

#
#  scores.csv の有無を検査する
#
    scorecsvpath = rootpath / "scores.csv"
    if not scorecsvpath.exists():
        print("scores.csv がありません.")
        dmy = input("終了します")
        sys.exit()
    if not scorecsvpath.is_file():
        print("このフォルダの scores.csv がファイルではありません")
        dmy = input("終了します")
        sys.exit()
#
#   採点結果を集積するフォルダのパスをつくる
#
    global gradingpath
    gradingpath = rootpath / "grading"
#
#   添付ファイル集積フォルダと採点結果集積フォルダをつくる
#
    if not gradingpath.exists():
        gradingpath.mkdir()


#    outputpath = rootpath / args.output

    # rootpathの名前をタイトルにする
    assignmentname = rootpath.absolute().name
#
# 終了のためのプロンプト
#
    loadscorescsv()
    dmy = input("終了します")
