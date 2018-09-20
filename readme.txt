//由于需要配合中文输入法，因此需要打包百度输入法一起安装
那么发布操作步骤是：
１．拷贝百度输入法到assets 目录，注意修改用到输入法apk对应代码的名字
２．在AndroidMenifest.xml文件增加system权限android:sharedUserId="android.uid.system"

然后拷贝app-debug.apk 重新采用系统签名，继而发布apk


//平时测试步骤：
删除掉上面的apk文件，已经去除系统权限


//增加代码混淆
storeFile 指定路径，文件在项目下面，然后编译，再用系统签名


###################发布版本步骤####################
1.改变编译类型，默认是debug版本，需要修改为release版本
2.最终生产apk-->build/outputs/apk/app-release.apk
3.上传签名后的apk到FTP备份

###################发布版本步骤######################
