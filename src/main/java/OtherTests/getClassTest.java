package OtherTests;

import java.util.Date;

public class getClassTest extends Date {

	static int x;
	
	public static void main(String[] args){
		System.out.println(a());
		System.out.println(x);
	}
	
	
	public static int a(){
		x=1;
		try{
			return x;
		}finally{
			x++;
		}
	}
}
