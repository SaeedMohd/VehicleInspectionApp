package com.matics.command;

import java.math.BigInteger;

import android.util.Log;

import com.matics.MainActivity;

public class    VinObdCommand extends IntObdCommandDecodeVin {
	public VinObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}

	public VinObdCommand(VinObdCommand other) {
		super(other);
	}

	public String formatResult() {
		String res = super.formatResult();

//		if (res.contains("NODATA") || res.contains("NO DATA")) {
//			return "NODATA";
//		}

		//Log.dMainActivity.TAG, "VIn Value = "+res);

		if (res.contains("\n")) {
			String[] ress = res.split("\n");
			res = ress[1];
		}
		
		//Log.e("VIN res 1", res);
//		res = res.replace(" ", "");
		//Log.e("VIN res 2", res);
		try {
//			 res = "0140:4902014D414C1:42423531524C432:4D343330363930";
//			 res = "0140:49020131474E1:464B31333539382:52333233383634";
			 
			//res = "1GNFK13598R323666";
			 // Demo Vin is : 1GNFK13598R223864
//			  Demo Vin is : 1GNFK13598R323864
//			  07-04 15:20:23.157: E/TAG(28876): res b1:0140:4902014D414C1:42423531524C432:4D343330363930

			 
			 
			String[] temp = res.split(":");
			// String Line1 = temp[1].substring(0, temp[1].length()-1);
			// String Line2 = temp[2].substring(0, temp[1].length()-2);
			String temp1 = "";
			for (int i = 1; i < temp.length; i++) {
				if (i == temp.length - 1) {
					temp1 = temp1.concat(temp[i]);
				} else {
					temp1 = temp1.concat(temp[i].substring(0,
							temp[i].length() - 1));
				}
			}
			temp1 = temp1.substring(6, temp1.length());
			//Log.e("", "temp1 is :" + temp1);
			//Log.e("", "Asci is :" + convertHexToString(temp1));

			// String byteStrOne = res.substring(4,6);
			// String byteStrTwo = res.substring(6,8);

			return convertHexToString(temp1);
		} 
		catch (Exception ex) {
			// TODO: handle exception
			//Log.e("", "Error" + ex.getMessage());
			return res;
		}

	}
	public String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}
		System.out.println("Decimal : " + temp.toString());

		return sb.toString();
	}

	// public int transform(int b) {
	// //Log.e("", "value of b is :" + b);
	// return b;
	//
	// }
}
