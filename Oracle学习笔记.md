流程控制学习：
      打开 command window,切换到Editor模式，复制代码到dialog中，并添加一个/,enter看到执行结果
      
      
	1.
	set serverout on; //为了可以在dialog中看到输出结果
	begin
		dbms_output.put_line('hello');
	 end;


	 2.
	set serverout on;
	declare n number :=1;
	begin
		dbms_output.put_line('hello'|| n);
	 end;


	 3.
	set serverout on;
	set serverout on;
	declare n number :=1;
					name varchar2(20) :='word';
	begin
		dbms_output.put_line('hello'|| n || name);
	 end;


	 4.简单的if-else查询
	 set serverout on;
	 declare emp_count number;
	 begin
		 select count(*) into emp_count from EMP where sal >=3000;
		 if emp_count > 0 then
			 dbms_output.put_line('有员工' || emp_count || '工资大于3000');
			else
				dbms_output.put_line('没有员工工资大于3000');
			 end if;
	 end;


	 5.多个else if 查询，当有多少个if 出现，就得end if多少个匹对
	 set serverout on;
	 declare emp_count number;
	 begin
		 select count(*) into emp_count from EMP where sal >=3000;
		 if emp_count =1 then
			 dbms_output.put_line('有员工1个，工资大于3000');
		 else if emp_count > 1 then
			 dbms_output.put_line('有多于1个员工，工资大于3000');
		 else
				dbms_output.put_line('没有员工工资大于3000');
		 end if;
		 end if;
	 end;


	 6.case 使用
	 set serverout on;
	 declare emp_count number;
	 begin
		 select count(*) into emp_count from EMP where sal >=3000;
		 case emp_count
			 when 0 then dbms_output.put_line('没有员工工资大于3000');
			 when 1 then dbms_output.put_line('有员工1个，工资大于3000');
			 when 2 then dbms_output.put_line('有员工2个，工资大于3000');
			 when 3 then dbms_output.put_line('有员工3个，工资大于3000');
			 when 4 then dbms_output.put_line('有员工大于3个，工资大于3000');
			end case;
		end;


	7.loop的使用
		set serverout on;
		declare g_id number :=1;
						g_losal number;
						g_hisal number;
		 begin
			 loop
				 if(g_id>4) then
						 exit;
					end if;
					select losal,hisal into g_losal,g_hisal from salgrade where grade=g_id;
					dbms_output.put_line(g_id || '等级的最低薪资' || g_losal || '最高工资' || g_hisal);
					g_id := g_id+1;
				end loop;
			end;

	8.while的使用
		set serverout on;
		declare g_id number :=1;
						g_losal number;
						g_hisal number;
		 begin
				while g_id<5 loop
					select losal,hisal into g_losal,g_hisal from salgrade where grade=g_id;
					dbms_output.put_line(g_id || '等级的最低薪资' || g_losal || '最高工资' || g_hisal);
					g_id := g_id+1;
				end loop;
			end;

	 9.for 的使用
		set serverout on;
		declare g_id number :=1;
						g_losal number;
						g_hisal number;
		 begin
				for g_id in 2..4 loop
					select losal,hisal into g_losal,g_hisal from salgrade where grade=g_id;
					dbms_output.put_line(g_id || '等级的最低薪资' || g_losal || '最高工资' || g_hisal);
				end loop;
			end;


	 10.简单的游标的使用
		 set serverout on;
			declare cursor cu_emp is
							select empno,ename,sal from emp;
				e_no number;
				e_name varchar2(10);
				e_sal number;
			begin
				open cu_emp;
				fetch cu_emp into e_no,e_name,e_sal;
				while cu_emp%found loop
							dbms_output.put_line('编号'|| e_no || '姓名'|| e_name ||'基本薪资'|| e_sal);
							fetch cu_emp into e_no,e_name,e_sal;
						end loop;
					close cu_emp;
			end;

		11.使用 tableName.attr%type 动态的指定变量的类型与表字段的类型一样
		set serverout on;
			declare cursor cu_emp is
							select empno,ename,sal from emp;
				e_no emp.empno%type;
				e_name emp.ename%type;
				e_sal emp.sal%type;
			begin
				open cu_emp;
				fetch cu_emp into e_no,e_name,e_sal;
				while cu_emp%found loop
							dbms_output.put_line('编号'|| e_no || '姓名'|| e_name ||'基本薪资'|| e_sal);
							fetch cu_emp into e_no,e_name,e_sal;
						end loop;
					close cu_emp;
			end;
          
          
	12.使用 tabkeName%rowtype 动态的指定一个变量，并且该变量为表所有的字段的类型，相当于row,减少变量的定义
	set serverout on;
	declare cursor cu_emp is
					select * from emp;
	 e_row emp%rowtype;  
	begin
		open cu_emp;
		fetch cu_emp into e_row;
		while cu_emp%found loop
					dbms_output.put_line('编号'|| e_row.empno || '姓名'|| e_row.ename ||'基本薪资'|| e_row.sal);
					fetch cu_emp into e_row;
				end loop;
			close cu_emp;
	end;

	13.隐式游标的使用  sql%isopen boolean值，隐式游标打开为true 默认关闭
	 set serverout on;
	begin
		if sql%isopen then
			dbms_output.put_line('游标打开');
		 else
			 dbms_output.put_line('游标关闭');
			end if;
		end;

	14.动态游标的使用  type  is ref cursor return emp%rowtype; 定义一个类型为游标并且指定游标的返回数据的类
	 set serverout on;
		declare type emptype is ref cursor return emp%rowtype;  //指定了该游标只能是表emp的行类型
		cu_emp emptype; // 定义这个类的一个变量
		e_count number;
		e emp%rowtype;
		begin
			select count(*) into e_count from emp where job='PRESIDENT';
			if e_count > 0 then  //如果大于0，cu_emp就是返回表的都有数据
				open cu_emp for select * from emp;  //
			else   //没有那个人，cu_emp返回的就是符合条件的数据
				open cu_emp for select * from emp where job='PRESIDENT';
			 end if;
			 fetch cu_emp into e;
						 while cu_emp%found loop
							 dbms_output.put_line('编号'|| e.empno || '姓名'|| e.ename ||'基本薪资'|| e.sal);
							 fetch cu_emp into e;
							 end loop;
					close cu_emp;
			end;

	 15.动态游标的弱类型 不指定游标的数据类型，根据业务指定
	 set serverout on;
		declare type customType is ref cursor;
			 e_count number;
			 e emp%rowtype;
			 s salgrade%rowtype;
			 cType customType;
			 begin
				 select count(*) into e_count from emp where job='PRESIDENT';
				 if e_count > 0 then
					 open cType for select * from salgrade;
							 fetch cType into s;
									while cType%found loop
									 dbms_output.put_line(s.grade || '等级的最低薪资' || s.losal || '最高工资' || s.hisal); 
										fetch cType into s;
										end loop;
									 close cType;
					else
						open cType for select * from emp;
							fetch cType into e;
									while cType%found loop
									 dbms_output.put_line('编号'|| e.empno || '姓名'|| e.ename ||'基本薪资'|| e.sal);
										fetch cType into e;
										end loop;
									 close cType;
						end if;

				 end;


	 16.函数的基本用法
	 create function getBookCount return number as
			begin
				declare book_count number;
				begin
					select count(*) into book_count from t_book;
					return book_count;
				end;
			end getBookCount;

	 17.带参数的函数的基本用法
	 create function getTableCount(table_name varchar2) return number as
			begin
				declare recore_count number;
				query_sql varchar2(300);
				begin
					query_sql:='select count(*) from ' || table_name;  //将用户传递进的表名拼接
					execute immediate query_sql into recore_count;  //执行语句并将结果赋给recore_count
					return recore_count;
				end;
			end getTableCount;

	 18.存储过程的基本用法 
	 create procedure addBook(bookName in varchar2,typeId in number) as
			begin
				declare maxId number;
				begin
					select max(id) into maxId from t_book;
					insert into t_book values(maxId+1,bookName,typeId);
					commit;
				end;
			end addBook;

	 19.带判断的存储过程的基本用法 
	 create procedure addBook2(bN in varchar2,typeId in number) as
			begin
				declare maxId number;
				n number;
				begin
					select count(*) into n from t_book where bookName=bN;
					if(n>0) then  //如果指定的bookName存在，就返回
					 return;
					end if;  //不存在就插入
					select max(id) into maxId from t_book;
					insert into t_book values(maxId+1,bN,typeId);
					commit;
				end;
			end addBook2;

	20.带输入输出参数的存储过程的基本用法
	 create procedure addBook3(bN in varchar2,typeId in number,n1 out number,n2 out number) as
			begin
				declare maxId number;
				n number;
				begin
					select count(*) into n1 from t_book;
					select count(*) into n from t_book where bookName=bN;
					if(n>0) then
					 return;
					end if;
					select max(id) into maxId from t_book;
					insert into t_book values(maxId+1,bN,typeId);
					select count(*) into n2 from t_book;
					commit;
				end;
			end addBook3;

		21.触发器
			 表级触发器
			 create trigger tr_book
		before insert
		on t_book     //在对该表进行插入时触发
		begin
			if user!='cc' then   //如果用户名不是cc,触发输出语句
				raise_application_error(-20001,'权限不足');
			end if;
		end;

		 create trigger tr_book2
		before update or delete
		on t_book    //在对表进行更新时触发
		begin
			if user!='CC' then
				raise_application_error(-20001,'权限不足');
			end if;
		end;


	create trigger tr_book_log
	after insert or update or delete
	on t_book       //在对表进行插入或者更新时触发
	begin
		if updating then   //进行更新时
			insert into t_book_log values(user,'update',sysdate);
		else if inserting then   //进行插入时
			insert into t_book_log values(user,'insert',sysdate);
		else if deleting then   //进行删除时
			insert into t_book_log values(user,'delete',sysdate);
		end if;
		end if;
		end if;
	end;



	 行内触发器，与表级触发器语法差不多，多一个for each row
	 create trigger tr_book_add
		after insert
		on t_book
		for each row
		begin
			update t_booktype set num=num+1 where id=:new.typeId;
		end;


	22.权限以及授权
	Oracle 用户分两种，一种是系统用户 sys system ；另外一种是普通用户；
	视图 dba_users 存储着所有用户信息；
	
	创建用户：
	Create user 用户名 identified by 密码 default tablespace 表空间
	create user TEST identified by 123456 default tablespace users;
	
	授予 session 权限：grant create session to TEST;
	
	锁定和开启帐号：alter user TEST account lock / unlock ;
	
	修改用户密码：alter user TEST identified by 123 ;
	
	删除用户： drop user TEST cascade ; 删除用户，并且把用户下的对象删除，比如表，视图，触发器等
	
	Oracle 权限分为系统权限和对象权限；
	系统权限是 Oracle 内置的，与具体对象无关的权限，比如创建表的权限，连接数据库权限；
	对象权限就是对具体对象，比如表，视图，触发器等的操作权限；
	系统权限视图：system_privilege_map
	权限分配视图：dba_sys_privs
	
	回收系统权限 revoke 权限 from 用户
	
	对象权限分配
	用户表权限视图：dba_tab_privs
	
	给对象授权 grant 权限 on 对象 to 用户 with grant option;
	
	回收权限：revoke 对象权限 on 对象 from 用户；
	
	角色是权限的集合；可以给用户直接分配角色，不需要一个一个分配权限；
	语法：
	Create role 角色名称；
	使用视图 dba_roles 可以查找角色信息
	
	23.包
	
	引入的目的，是为了有效的管理函数和存储过程，当项目模块很多的时候，用程序包管理就很有效了。
	语法：
	
	Create or replace package 包名 as
	变量名称 1 数据类型 1；
	变量名称 2 数据类型 2；
	...
	...
	Function 函数名称 1(参数列表) return 数据类型 1；
	Function 函数名称 2(参数列表) return 数据类型 2；
	...
	...
	Procedure 存储过程名称 1(参数列表)；
	Procedure 存储过程名称 2(参数列表)；
	
	End 报名;
		
		
		
		
	//genCVS File & update fileName on RIU_REPORT_ENQ_LOG 
	public void onExport2CVS(){
		final String THIS_METHOD = "onExport2CVS";
		List<PowerUsageTO> powerUsageList = this.getPowerUsageList().getValueList();
		System.out.println("start gen cvs");
		HttpServletResponse response = (HttpServletResponse) FacesContextUtil.getExternalContext().getResponse();
		response.setContentType("text/csv");
		String fileName = "Power_Usage_"+powerUsageList.get(0).getPackNo()+"_"+powerUsageList.get(0).getBeginUsageDate()+"-"+powerUsageList.get(0).getEndUsageDate()+".csv";
		response.setHeader("Content-disposition", "attachment; filename=\"" + fileName+"\"");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println(this.genSearchList2CSV(powerUsageList));
			out.flush();
			out.close();
			this.reportEnqLogTO.setGenFlieName(fileName);
			//update riu_report_enq_log gen_file_name & gen_flag
			IReportFacade iReportFacade = (IReportFacade) WttSpringAppContextUtil.getBean(ReportConstant.RIU_REPORT_PROXY);
			iReportFacade.updFileNameOnReportLog(reportEnqLogTO);
			System.out.println("gen cvs success");
		} catch (IOException e) {
			iLog.error(THIS_METHOD, "error writing output for cvs export");
		}
		FacesContextUtil.getFacesContext().responseComplete();
	}
	
	private String genSearchList2CSV(List<PowerUsageTO> powerUsageList){
		final String THIS_METHOD = "genSearchList2CSV";
		SimpleDateFormat spFormat = new SimpleDateFormat("yyyy/MM/dd");
		StringWriter sWriter = new StringWriter();
		try {
			ExcelCSVPrinter ecp = new ExcelCSVPrinter(sWriter);
			ecp.write("Rack No.");
			ecp.write("Usage Date");
			ecp.write("KVA");
			ecp.writeln();
			for (int i = 0; i < powerUsageList.size(); i++) {
				PowerUsageTO item = (PowerUsageTO)powerUsageList.get(i);
				ecp.write(item.getPackNo());
				ecp.write(spFormat.format(item.getUsageDate()));
				ecp.write(item.getPowerKVAMax());
				ecp.writeln();
			}
		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(),e);
		}
		return sWriter.toString();
	}	
		
		
                 
               
