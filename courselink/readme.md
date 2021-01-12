# 前提条件

build後、$CATALINA_HOME/lib内に、「jstl-1.2.jar」が配置されているか確認してください。
未配置であれば、
「.m2\repository\javax\servlet\jstl\1.2」
内の
「jstl-1.2.jar」
を$CATALINA_HOME/libに配置してからdeployしてください。

## サイトIDフォーマット
リクエストを送る際のサイトIDの様式は、[aaaa-bbb-cccc-ddd]とし、「bbb」に当たる数値を部局コードとする。

# 使用property
## コースリンクサイトのデフォルトテンプレート

###### サイトID:!courselinkをデフォルトテンプレートする場合

courselink.templatesite=!courselink

## サイト作成の際のサイトID

##### 科目コードをサイトIDにする場合
courselink.siteid.type=readable
##### ランダムな英数字をサイトIDにする場合
courselink.siteid.type=uuid

## 部局ごとのテンプレート、初心者用テンプレート対応用
### 部局ごとのテンプレート指定
courselink.templatesite.xxx=部局コードxxxのテンプレートサイト
のように記述して、部局ごとのテンプレートサイトIDを宣言する。

##### 例：部局コード111のテンプレートサイトを宣言する場合
courselink.templatesite.111=!courselink.template.111

宣言されたIDのサイトは、別途作成必要。

### 初心者用テンプレート指定
courselink.templatesite.beginner=初心者用テンプレートサイトID

##### サイトID:[!courselink.template.beginner]を初心者用テンプレートとする場合
courselink.templatesite.beginner=!courselink.template.beginner

##### サイト作成後に、courselink_requestのstatusを更新する際の初心者用コード
courselink.templatesite.beginner.status=9999

部局ごとのテンプレート指定の時と同じように、初心者用のテンプレートサイトも別途作成必要。
