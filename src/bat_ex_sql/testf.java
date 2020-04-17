package bat_ex_sql;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

public class testf extends JFrame implements ActionListener {
	//控件
	private JTextArea text_information;
	private JPasswordField text_pwd;
	private JTextField text_url,text_port,text_name,text_user,text_sql,text_txt;
    private JButton button_sql,button_txt,button_start; 
    private JFileChooser fc=new JFileChooser();
	JFrame frame = new JFrame("提示框");
	JComboBox cb_type=new JComboBox();
    //变量
    private static List<String> filelist= new ArrayList<String>();
	//完整的连接语句jdbc:oracle:thin:@localhost:1521:orcl
	private static String db="jdbc:oracle:thin:@";
	private String dburl="localhost";
	private String dbport="1521";
	private String dbname="orcl";//数据库名称
	private String dbuser="scott";//登录用户
	private String dbpwd="tiger";//登录密码
	private String sqlpath ="D:/";//脚本目录
	private String clobpath ="D:/";//数据文件目录
	//获取指定目录下的文件
	public static void SearchFile(String path){
		File folder=new File(path);
		if (!folder.exists() || !folder.isDirectory()) {// 判断是否存在目录
            return;
        }
		String[] files = folder.list();
		for (int i = 0; i < files.length; i++) {
            File file = new File(folder, files[i]);
            if (file.isFile()) {// 如果是文件
            	//判断是不是SQL文件
            	String[] strArray = file.getName().split("\\.");//用'.'分割需要使用转义字符\\  
                int Index = strArray.length -1;
                if(strArray[Index].equals("sql"))
                	filelist.add(folder + "\\" + file.getName());
//                	System.out.println(folder + "\\" + file.getName());
            } 
            else {// 如果是目录，可以继续调用自身获取其中的文件
//            	System.out.println("存在目录:"+folder + "\\" +file.getName());
//            	SearchFile(folder + "\\" + file.getName());
            }
        }
	}	
	//连接数据库
	public Connection getConnection(String url,String port,String name,String user,String pwd){
		Connection conn=null;
//		System.out.print(url+user+pwd);
		try{
			switch (cb_type.getSelectedIndex()){
				case 0://oracle
					Class.forName("oracle.jdbc.driver.OracleDriver");//加载数据库驱动
					conn=DriverManager.getConnection("jdbc:oracle:thin:@"+url+":"+port+":"+name, user, pwd);
					if(conn!=null){
						text_information.setText(text_information.getText()+"\r\n"+"成功连接数据库");
//						System.out.println("成功连接数据库");
					}
					else
					{
						text_information.setText(text_information.getText()+"\r\n"+"未能连接数据库，请检查参数是否正确");
//						System.out.println("未能连接数据库，请检查参数是否正确");
					}
					break;
				case 1://mysql 8.0版本以下
				    Class.forName("com.mysql.jdbc.Driver");
				    conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/"+name, user, pwd);
					if(conn!=null){
						text_information.setText(text_information.getText()+"\r\n"+"成功连接数据库");
//						System.out.println("成功连接数据库");
					}
					else
					{
						text_information.setText(text_information.getText()+"\r\n"+"未能连接数据库，请检查参数是否正确");
//						System.out.println("未能连接数据库，请检查参数是否正确");
					}
					break;
				case 2://mysql 8.0版本以上
				    Class.forName("com.mysql.cj.jdbc.Driver");
				    conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/"+name+"?useSSL=false&serverTimezone=UTC", user, pwd);
					if(conn!=null){
						text_information.setText(text_information.getText()+"\r\n"+"成功连接数据库");
//						System.out.println("成功连接数据库");
					}
					else
					{
						text_information.setText(text_information.getText()+"\r\n"+"未能连接数据库，请检查参数是否正确");
//						System.out.println("未能连接数据库，请检查参数是否正确");
					}
					break;
				default : 	
			}
			
		}catch (Exception e){
			text_information.setText(text_information.getText()+"\r\n"+"未能连接数据库，请检查连接参数是否正确");
//			e.printStackTrace();
		}
		return conn;
	}
	//获取指定文件的内容
	public static String readFile(String path,int flag){//关于这个flag传输的参数，涉及中文乱码，因为我的sql文件都是ansi格式的要用GBK打开，txt都是utf-8格式的，所以要区分开
		
		
		StringBuffer str = new StringBuffer();
        BufferedReader reader = null;
        try {
            if(flag==0)
            	reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "GBK"));
            else
            	reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	//正则匹配文件注释并删掉
            	if(tempString.matches("--.*"))
            		continue;
                str = str.append(" " + tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return str.toString();
	}
	
	public testf() {
		super("批量执行sql文件");                            //窗口标题
		//可以弹出一个提示框信息
//		frame.setLayout(null);
//		frame.setBounds(20,20,300,200);
//		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//		frame.setResizable(false);   
		text_information=new JTextArea(15,55);
		text_information.setLineWrap(true);
		JScrollPane panelOutput;
		panelOutput = new JScrollPane(text_information);
		panelOutput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
//		frame.add(panelOutput);
//		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));//内容窗格流布局且右对齐
		//设置登录框
        this.setBounds(20,20,810,450);
        this.setResizable(false);                          //窗口大小不能改变
//        this.setBackground(java.awt.Color.lightGray);		//换个皮肤
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);      //单击窗口关闭按钮时，结束程序运行
        this.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));//内容窗格流布局且右对齐
        //	各个控件
        cb_type.addItem("oracle");
        cb_type.addItem("mysql8.0以下");
        cb_type.addItem("mysql8.0以上");
        this.getContentPane().add(cb_type);
        this.getContentPane().add(new JLabel("地址"));      
        text_url = new JTextField("127.0.0.1",10);
        this.getContentPane().add(text_url);
        this.getContentPane().add(new JLabel("端口"));      
        text_port = new JTextField("1521",5);
        this.getContentPane().add(text_port);
        this.getContentPane().add(new JLabel("数据库名称"));     
        text_name = new JTextField("",6);
        this.getContentPane().add(text_name);
        this.getContentPane().add(new JLabel("用户名"));     
        text_user = new JTextField("",9);
        this.getContentPane().add(text_user);
        this.getContentPane().add(new JLabel("密码"));   
        text_pwd = new JPasswordField("",9);
        this.getContentPane().add(text_pwd);
                
        this.getContentPane().add(new JLabel("sql路径"));      //内容窗格添加组件
        text_sql=new JTextField(57);
        this.getContentPane().add(text_sql);
        button_sql=new JButton("设置sql路径");
        this.getContentPane().add(button_sql);
        button_sql.addActionListener(this);                //为按钮注册动作事件监听器
        this.getContentPane().add(new JLabel("txt数据路径"));      //内容窗格添加组件
        text_txt=new JTextField(55);
        this.getContentPane().add(text_txt);
        button_txt=new JButton("设置txt路径");
        this.getContentPane().add(button_txt);
        button_txt.addActionListener(this);                //为按钮注册动作事件监听器
        button_start=new JButton("开始执行");
        this.getContentPane().add(button_start);
        button_start.addActionListener(this);                //为按钮注册动作事件监听器
        this.getContentPane().add(new JLabel("txt数据路径为空则默认与sql文件在同一文件夹下"));      //内容窗格添加组件
        this.add(panelOutput);
		this.setVisible(true);
	}
	//按钮响应事件
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==button_sql)
		{//点击设置SQL文件路径
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int intRetVal = fc.showOpenDialog(this);
			if (intRetVal == JFileChooser.APPROVE_OPTION) {
			text_sql.setText(fc.getSelectedFile().getPath());
			}
		}else if(e.getSource()==button_txt)
		{//点击设置txt数据文件路径
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int intRetVal = fc.showOpenDialog(this);
			if (intRetVal == JFileChooser.APPROVE_OPTION) {
			text_txt.setText(fc.getSelectedFile().getPath());
			}
		}else if(e.getSource()==button_start)
		{//点击按钮开始执行sql
//			frame.setVisible(true);
			Date date = new Date();
			SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
			text_information.setText("执行时间："+dateFormat.format(date));
			
			sqlpath =text_sql.getText();//脚本目录
			if(text_txt.getText().equals(""))
				clobpath =text_sql.getText();//数据文件目录
			else
				clobpath =text_txt.getText();//数据文件目录
			dburl=text_url.getText();
			dbport=text_port.getText();
			dbname=text_name.getText();//数据库名称
			dbuser=text_user.getText();//登录用户
			dbpwd=text_pwd.getText();//登录密码
			SearchFile(sqlpath);
			if(filelist.size()==0)
			{
				text_information.setText(text_information.getText()+"\r\n"+"该目录下没有sql文件");
//				System.out.println("该目录下没有sql文件");
				return ;
			}
			else
			{
//				text_information.setText("该目录下有"+filelist.size()+"个文件");
			}
			//连接数据库
			Connection conn=getConnection(dburl,dbport,dbname,dbuser,dbpwd);
			if (conn==null)
				return ;
			//存储执行的语句总个目和失败的个数
			long sum=0,error=0;
			//遍历所有SQL文件，执行语句
			try{
				Statement st = conn.createStatement();
				//java不能将;当为结束符，需要以分号为间隔，单条执行语句
				String allsql;
				for(String file:filelist)
				{
					text_information.setText(text_information.getText()+"\r\n"+"正在执行:"+file);
//					System.out.println(file);//sql文件地址
					allsql =readFile(file,0);
					String[] sqlArray= allsql.split(";");
					for(String sql:sqlArray)
					{
						try{
//							System.out.println(sql);//要执行的sql语句
							if(sql.length()>1)//判断读取的SQL文件内容不是空的
							{
								sum++;
								//匹配${*******}$判断有没有clob文件   .compile("\\$\\{.+(?:\\}\\$)");
								//匹配dbvis***.txt把对应的txt文件存进去   .compile("dbvis.+(?:txt)");
								Pattern pattern = Pattern.compile("dbvis.+(?:txt)");  
						        Matcher matcher = pattern.matcher(sql);  
						        if (matcher.find()) {  
						        	sql=sql.replaceAll("\\$\\{.+(?:\\}\\$)","?");
						        	PreparedStatement stat = conn.prepareStatement(sql); 
						        	File testFile = new File(clobpath+"\\"+matcher.group(0));
						        	if(!testFile.exists())
						        	{
						        		text_information.setText(text_information.getText()+"\r\n"+clobpath+"\\"+matcher.group(0)+"该数据文件不存在，执行失败"+"\r\n"+error);
						        		error++;//统计出错语句条数
						        		continue;
						        	}
						        	//以文件流的形式把clob数据存进去
						            String clobContent=readFile(clobpath+"\\"+matcher.group(0),1);
						            StringReader reader = new StringReader(clobContent);  
						            stat.setCharacterStream(1, reader, clobContent.length());  
						            stat.executeUpdate();
						        }
						        else{
							       st.executeQuery(sql);
						        }
							}
						}catch (Exception ex){
//							ex.printStackTrace();
							//把出错的SQL语句显示出来
							text_information.setText(text_information.getText()+"\r\n"+sql+"执行失败\r\n"+"失败原因:"+ex.toString()+"\r\n"+error);
							//仅显示错误
//							text_information.setText(text_information.getText()+"\r\nSQL语句执行失败\r\n"+"失败原因:"+ex.toString());	
							error++;//统计出错语句条数
							continue ;
						}
//						
					}
					text_information.setText(text_information.getText()+"\r\n"+file+"执行结束");
				}
				conn.close();
				st.close();
			}catch (Exception ex){
				//这里应该只能捕获到connection变量和statement变量的错误
				text_information.setText(text_information.getText()+"\r\n未定义的错误\r\n"+ex.toString());
			}
			//所有文件执行完毕
			text_information.setText(text_information.getText()+"\r\n该目录下所有sql文件已执行完毕");
			text_information.setText(text_information.getText()+"\r\n执行情况:总执行"+sum+"条，成功"+String.valueOf(sum-error)+"条，失败"+error+"条");
			
		}
	}
	public static void main(String[] args) {
		new testf();
	}

}