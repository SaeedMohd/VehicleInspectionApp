package com.matics.command;

import android.util.Log;

import com.matics.MainActivity;


public class TroubleCodesObdCommand7 extends ObdCommand {

	protected final static char[] dtcLetters = {'P','C','B','U'};
	private StringBuffer codes = null;
	public TroubleCodesObdCommand7() {
		super("03","Trouble Codes","");
		codes = new StringBuffer();
	}
	public TroubleCodesObdCommand7(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
		codes = new StringBuffer();
	}
	public TroubleCodesObdCommand7(TroubleCodesObdCommand7 other) {
		super(other);
		codes = new StringBuffer();
	}
	public void run() {
		DtcNumberObdCommand numCmd = new DtcNumberObdCommand();
		try {
			/*numCmd.setInputStream(in);
				numCmd.setOutputStream(out);
				numCmd.start();
				try {
					numCmd.join();
				} catch (InterruptedException e) {
					setError(e);
				}*/
			numCmd.setInputStream(in);
			numCmd.setOutputStream(out);
			numCmd.start();
			while (true) {
				numCmd.join(2);
				if (!numCmd.isAlive()) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		try {
			numCmd.formatResult();
			int count = numCmd.getCodeCount();
			int dtcNum = (count+2)/3;
			for (int i = 0; i < dtcNum; i++) {
				sendCmd(cmd);
				readResult();
				String res = getResult();
				//Log.dMainActivity.TAG, res);
				res = res.replace("\r", "");
				res = res.replace("SEARCHING...", "");
				//Log.e("", "trouble code is :" + res);
				if(res.contains("\n")){
					String[] ress = res.split("\n");
					res = ress[1];
				}	
				
				for (int j = 0; j < 3; j++) {
					String byte1 = res.substring(3+j*6,5+j*6);
					String byte2 = res.substring(6+j*6,8+j*6);
					int b1 = Integer.parseInt(byte1,16);
					int b2 = Integer.parseInt(byte2,16);
					int val = (b1 << 8) + b2;
					if (val == 0) {
						break;
					}
					String code = "P";
					if ((val&0xC000) > 14) {
						code = "C";
					}
					code += Integer.toString((val&0x3000)>>12);
					code += Integer.toString((val&0x0fff));
					codes.append(code);
					codes.append("\n");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			codes.append(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String formatResult() {
//		String res = getResult();
//		String[] ress = res.split("\r");
//		for (String r:ress) {
//			String k = r.replace("\r","");
//			codes.append(k);
//			codes.append("\n");
//		}
		return codes.toString();
	}
}
