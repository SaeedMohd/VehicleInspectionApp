package com.matics.command;

public class MultiplePidObdCommand extends ObdCommand {

	public MultiplePidObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public MultiplePidObdCommand(AbsoluteLoadValueObdCommand other) {
		super(other);
	}
	
	public String formatMultipleResults() {
		String res = getResult();
//		//Log.i("MultiplePidObdCommand::formatMultipleResults","Commd: "+cmd+", returned a response: "+res);
//		res= res.replace("\n", "");
//		res= res.replace("\r", "");
//		ArrayList<String> responseArrayList = new ArrayList<String>();
//		String[] responseArray = res.split(" ");
//		String availableBytesString = responseArray[0];
//		int availableBytesInteger = Integer.parseInt(availableBytesString, 16);
//		////Log.i("availableBytesInteger","availableBytesInteger = "+availableBytesInteger);
//		for (int x = 1; x < responseArray.length; x++) {
//			////Log.i("looping on values",""+responseArray[x]);
//			if (responseArrayList.size()<availableBytesInteger-1){
//				if(!responseArray[x].contains(":") && !responseArray[x].contains("41") && !responseArray[x].contains(" ")){
//					//Log.i("adding in array",""+responseArray[x]);
//					responseArrayList.add(responseArray[x]);
//				}
//			}
//		}
//
//	//Log.i("response array list size",""+responseArrayList.size());


		res = res.replace("\r", "");
		res = res.replace("SEARCHING...", "");
		return res;
	}
}