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
	//�ؼ�
	private JTextArea text_information;
	private JPasswordField text_pwd;
	private JTextField text_url,text_port,text_name,text_user,text_sql,text_txt;
    private JButton button_sql,button_txt,button_start; 
    private JFileChooser fc=new JFileChooser();
	JFrame frame = new JFrame("��ʾ��");
	JComboBox cb_type=new JComboBox();
    //����
    private static List<String> filelist= new ArrayList<String>();
	//�������������jdbc:oracle:thin:@localhost:1521:orcl
	private static String db="jdbc:oracle:thin:@";
	private String dburl="localhost";
	private String dbport="1521";
	private String dbname="orcl";//���ݿ�����
	private String dbuser="scott";//��¼�û�
	private String dbpwd="tiger";//��¼����
	private String sqlpath ="D:/";//�ű�Ŀ¼
	private String clobpath ="D:/";//�����ļ�Ŀ¼
	//��ȡָ��Ŀ¼�µ��ļ�
	public static void SearchFile(String path){
		File folder=new File(path);
		if (!folder.exists() || !folder.isDirectory()) {// �ж��Ƿ����Ŀ¼
            return;
        }
		String[] files = folder.list();
		for (int i = 0; i < files.length; i++) {
            File file = new File(folder, files[i]);
            if (file.isFile()) {// ������ļ�
            	//�ж��ǲ���SQL�ļ�
            	String[] strArray = file.getName().split("\\.");//��'.'�ָ���Ҫʹ��ת���ַ�\\  
                int Index = strArray.length -1;
                if(strArray[Index].equals("sql"))
                	filelist.add(folder + "\\" + file.getName());
//                	System.out.println(folder + "\\" + file.getName());
            } 
            else {// �����Ŀ¼�����Լ������������ȡ���е��ļ�
//            	System.out.println("����Ŀ¼:"+folder + "\\" +file.getName());
//            	SearchFile(folder + "\\" + file.getName());
            }
        }
	}	
	//�������ݿ�
	public Connection getConnection(String url,String port,String name,String user,String pwd){
		Connection conn=null;
//		System.out.print(url+user+pwd);
		try{
			switch (cb_type.getSelectedIndex()){
				case 0://oracle
					Class.forName("oracle.jdbc.driver.OracleDriver");//�������ݿ�����
					conn=DriverManager.getConnection("jdbc:oracle:thin:@"+url+":"+port+":"+name, user, pwd);
					if(conn!=null){
						text_information.setText(text_information.getText()+"\r\n"+"�ɹ��������ݿ�");
//						System.out.println("�ɹ��������ݿ�");
					}
					else
					{
						text_information.setText(text_information.getText()+"\r\n"+"δ���������ݿ⣬��������Ƿ���ȷ");
//						System.out.println("δ���������ݿ⣬��������Ƿ���ȷ");
					}
					break;
				case 1://mysql 8.0�汾����
				    Class.forName("com.mysql.jdbc.Driver");
				    conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/"+name, user, pwd);
					if(conn!=null){
						text_information.setText(text_information.getText()+"\r\n"+"�ɹ��������ݿ�");
//						System.out.println("�ɹ��������ݿ�");
					}
					else
					{
						text_information.setText(text_information.getText()+"\r\n"+"δ���������ݿ⣬��������Ƿ���ȷ");
//						System.out.println("δ���������ݿ⣬��������Ƿ���ȷ");
					}
					break;
				case 2://mysql 8.0�汾����
				    Class.forName("com.mysql.cj.jdbc.Driver");
				    conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/"+name+"?useSSL=false&serverTimezone=UTC", user, pwd);
					if(conn!=null){
						text_information.setText(text_information.getText()+"\r\n"+"�ɹ��������ݿ�");
//						System.out.println("�ɹ��������ݿ�");
					}
					else
					{
						text_information.setText(text_information.getText()+"\r\n"+"δ���������ݿ⣬��������Ƿ���ȷ");
//						System.out.println("δ���������ݿ⣬��������Ƿ���ȷ");
					}
					break;
				default : 	
			}
			
		}catch (Exception e){
			text_information.setText(text_information.getText()+"\r\n"+"δ���������ݿ⣬�������Ӳ����Ƿ���ȷ");
//			e.printStackTrace();
		}
		return conn;
	}
	//��ȡָ���ļ�������
	public static String readFile(String path,int flag){//�������flag����Ĳ������漰�������룬��Ϊ�ҵ�sql�ļ�����ansi��ʽ��Ҫ��GBK�򿪣�txt����utf-8��ʽ�ģ�����Ҫ���ֿ�
		
		
		StringBuffer str = new StringBuffer();
        BufferedReader reader = null;
        try {
            if(flag==0)
            	reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "GBK"));
            else
            	reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	//����ƥ���ļ�ע�Ͳ�ɾ��
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
		super("����ִ��sql�ļ�");                            //���ڱ���
		//���Ե���һ����ʾ����Ϣ
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
//		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));//���ݴ������������Ҷ���
		//���õ�¼��
        this.setBounds(20,20,810,450);
        this.setResizable(false);                          //���ڴ�С���ܸı�
//        this.setBackground(java.awt.Color.lightGray);		//����Ƥ��
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);      //�������ڹرհ�ťʱ��������������
        this.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));//���ݴ������������Ҷ���
        //	�����ؼ�
        cb_type.addItem("oracle");
        cb_type.addItem("mysql8.0����");
        cb_type.addItem("mysql8.0����");
        this.getContentPane().add(cb_type);
        this.getContentPane().add(new JLabel("��ַ"));      
        text_url = new JTextField("127.0.0.1",10);
        this.getContentPane().add(text_url);
        this.getContentPane().add(new JLabel("�˿�"));      
        text_port = new JTextField("1521",5);
        this.getContentPane().add(text_port);
        this.getContentPane().add(new JLabel("���ݿ�����"));     
        text_name = new JTextField("",6);
        this.getContentPane().add(text_name);
        this.getContentPane().add(new JLabel("�û���"));     
        text_user = new JTextField("",9);
        this.getContentPane().add(text_user);
        this.getContentPane().add(new JLabel("����"));   
        text_pwd = new JPasswordField("",9);
        this.getContentPane().add(text_pwd);
                
        this.getContentPane().add(new JLabel("sql·��"));      //���ݴ���������
        text_sql=new JTextField(57);
        this.getContentPane().add(text_sql);
        button_sql=new JButton("����sql·��");
        this.getContentPane().add(button_sql);
        button_sql.addActionListener(this);                //Ϊ��ťע�ᶯ���¼�������
        this.getContentPane().add(new JLabel("txt����·��"));      //���ݴ���������
        text_txt=new JTextField(55);
        this.getContentPane().add(text_txt);
        button_txt=new JButton("����txt·��");
        this.getContentPane().add(button_txt);
        button_txt.addActionListener(this);                //Ϊ��ťע�ᶯ���¼�������
        button_start=new JButton("��ʼִ��");
        this.getContentPane().add(button_start);
        button_start.addActionListener(this);                //Ϊ��ťע�ᶯ���¼�������
        this.getContentPane().add(new JLabel("txt����·��Ϊ����Ĭ����sql�ļ���ͬһ�ļ�����"));      //���ݴ���������
        this.add(panelOutput);
		this.setVisible(true);
	}
	//��ť��Ӧ�¼�
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==button_sql)
		{//�������SQL�ļ�·��
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int intRetVal = fc.showOpenDialog(this);
			if (intRetVal == JFileChooser.APPROVE_OPTION) {
			text_sql.setText(fc.getSelectedFile().getPath());
			}
		}else if(e.getSource()==button_txt)
		{//�������txt�����ļ�·��
		    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int intRetVal = fc.showOpenDialog(this);
			if (intRetVal == JFileChooser.APPROVE_OPTION) {
			text_txt.setText(fc.getSelectedFile().getPath());
			}
		}else if(e.getSource()==button_start)
		{//�����ť��ʼִ��sql
//			frame.setVisible(true);
			Date date = new Date();
			SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
			text_information.setText("ִ��ʱ�䣺"+dateFormat.format(date));
			
			sqlpath =text_sql.getText();//�ű�Ŀ¼
			if(text_txt.getText().equals(""))
				clobpath =text_sql.getText();//�����ļ�Ŀ¼
			else
				clobpath =text_txt.getText();//�����ļ�Ŀ¼
			dburl=text_url.getText();
			dbport=text_port.getText();
			dbname=text_name.getText();//���ݿ�����
			dbuser=text_user.getText();//��¼�û�
			dbpwd=text_pwd.getText();//��¼����
			SearchFile(sqlpath);
			if(filelist.size()==0)
			{
				text_information.setText(text_information.getText()+"\r\n"+"��Ŀ¼��û��sql�ļ�");
//				System.out.println("��Ŀ¼��û��sql�ļ�");
				return ;
			}
			else
			{
//				text_information.setText("��Ŀ¼����"+filelist.size()+"���ļ�");
			}
			//�������ݿ�
			Connection conn=getConnection(dburl,dbport,dbname,dbuser,dbpwd);
			if (conn==null)
				return ;
			//�洢ִ�е�����ܸ�Ŀ��ʧ�ܵĸ���
			long sum=0,error=0;
			//��������SQL�ļ���ִ�����
			try{
				Statement st = conn.createStatement();
				//java���ܽ�;��Ϊ����������Ҫ�Էֺ�Ϊ���������ִ�����
				String allsql;
				for(String file:filelist)
				{
					text_information.setText(text_information.getText()+"\r\n"+"����ִ��:"+file);
//					System.out.println(file);//sql�ļ���ַ
					allsql =readFile(file,0);
					String[] sqlArray= allsql.split(";");
					for(String sql:sqlArray)
					{
						try{
//							System.out.println(sql);//Ҫִ�е�sql���
							if(sql.length()>1)//�ж϶�ȡ��SQL�ļ����ݲ��ǿյ�
							{
								sum++;
								//ƥ��${*******}$�ж���û��clob�ļ�   .compile("\\$\\{.+(?:\\}\\$)");
								//ƥ��dbvis***.txt�Ѷ�Ӧ��txt�ļ����ȥ   .compile("dbvis.+(?:txt)");
								Pattern pattern = Pattern.compile("dbvis.+(?:txt)");  
						        Matcher matcher = pattern.matcher(sql);  
						        if (matcher.find()) {  
						        	sql=sql.replaceAll("\\$\\{.+(?:\\}\\$)","?");
						        	PreparedStatement stat = conn.prepareStatement(sql); 
						        	File testFile = new File(clobpath+"\\"+matcher.group(0));
						        	if(!testFile.exists())
						        	{
						        		text_information.setText(text_information.getText()+"\r\n"+clobpath+"\\"+matcher.group(0)+"�������ļ������ڣ�ִ��ʧ��"+"\r\n"+error);
						        		error++;//ͳ�Ƴ����������
						        		continue;
						        	}
						        	//���ļ�������ʽ��clob���ݴ��ȥ
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
							//�ѳ����SQL�����ʾ����
							text_information.setText(text_information.getText()+"\r\n"+sql+"ִ��ʧ��\r\n"+"ʧ��ԭ��:"+ex.toString()+"\r\n"+error);
							//����ʾ����
//							text_information.setText(text_information.getText()+"\r\nSQL���ִ��ʧ��\r\n"+"ʧ��ԭ��:"+ex.toString());	
							error++;//ͳ�Ƴ����������
							continue ;
						}
//						
					}
					text_information.setText(text_information.getText()+"\r\n"+file+"ִ�н���");
				}
				conn.close();
				st.close();
			}catch (Exception ex){
				//����Ӧ��ֻ�ܲ���connection������statement�����Ĵ���
				text_information.setText(text_information.getText()+"\r\nδ����Ĵ���\r\n"+ex.toString());
			}
			//�����ļ�ִ�����
			text_information.setText(text_information.getText()+"\r\n��Ŀ¼������sql�ļ���ִ�����");
			text_information.setText(text_information.getText()+"\r\nִ�����:��ִ��"+sum+"�����ɹ�"+String.valueOf(sum-error)+"����ʧ��"+error+"��");
			
		}
	}
	public static void main(String[] args) {
		new testf();
	}

}