# 功能
java读取sql文件到oracle或mysql中执行  
指定一个目录，读取其中的sql文件并执行，若sql语句中有clob数据，把对应的txt文件输入进去  
可以批处理提交sql语句  
# 注意事项
要注意文件格式，否则sql语句执行时会出现中文乱码的情况  
因为我的sql文件是ansi格式的,txt数据文件也是ansi的，如果某种文件是utf-8的，需要更代码  
更改使用readFile(文件路径,flag)时的传参  
flag为0打开ansi或gbk格式文件,flag为1打开utf-8文件  
