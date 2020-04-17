# 功能
java读取sql文件到oracle中执行
指定一个目录，读取其中的sql文件并执行，若sql语句中有clob数据，把对应的txt文件输入进去
# 注意事项
sql语句执行时出现中文乱码的情况
因为我的sql文件是ansi格式的，txt数据文件是utf-8的，所以打开方式不一样，有需要更改的话
对于sql文件，可以把代码第229行中函数传参的第二个数据改成0，就是读取utf8的文件
对于txt文件，可以把代码第245行中函数传参的第二个数据改成1，就是读取ansi或gbk格式文件
