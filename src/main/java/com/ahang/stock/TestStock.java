package com.ahang.stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ahang.utils.JsonUtils;

public class TestStock {

	public static void main(String args[]) throws Exception {
		String filePath = "C:\\Users\\liuhang\\Desktop\\temp\\list.txt";
		String loginurl = "https://xueqiu.com/user/login";
		
		StockService service = new StockService();
		//String token = service.getToken("liuahang@163.com", "ahang123457");
		String token = "35e959d5a61b2c2a5f52a8b3fe47c3d1430ab102";
		double sh000016 = getDiffer(service,token,"SH000016");
		double sz399006 = getDiffer(service,token,"SZ399006");
		if(sh000016<0&&sz399006<0){
			System.out.println("上证50涨幅:"+sh000016 + ",创业板指数涨幅："+sz399006 + ",持有：银华日利");
		}else if(sh000016<0&&sz399006>0){
			System.out.println("上证50涨幅:"+sh000016 + ",创业板指数涨幅："+sz399006 + ",持有：创业板");
		}else if(sh000016>0&&sz399006<0){
			System.out.println("上证50涨幅:"+sh000016 + ",创业板指数涨幅："+sz399006 + ",持有：上证50");
		}else if(sh000016>0&&sz399006>0){
			if(sh000016>sz399006){
				System.out.println("上证50涨幅:"+sh000016 + ",创业板指数涨幅："+sz399006 + ",持有：上证50");
			}else{
				System.out.println("上证50涨幅:"+sh000016 + ",创业板指数涨幅："+sz399006 + ",持有：创业板");
			}
			
		}
		//?symbol=SZ399006&period=1day&type=normal&begin=1449995265865&end=1481531265865&_=1481531265
    	//String begin = "1275321600000";//
    	//String begin = "401123200000";//
		//String begin = String.valueOf(System.currentTimeMillis()-3500*24*60*60*1000l);
		//String end = String.valueOf(System.currentTimeMillis());
		//String begin = String.valueOf(System.currentTimeMillis() - 40*24*60*60*1000l);
		//上证50 SH000016 创业板SZ399006  美国标普SP500  拉斯达克NASDAQ:QQQ  道琼斯指数(INDEXDJX:DJI30)
		//String stockInfo = service.getInfo(token,"SH000016",begin,end);
		//System.out.println("stockInfo==" + stockInfo);
		//Map map = JsonUtils.fromJson(stockInfo, Map.class);
		//System.out.println("map==" + map);
		//StockInfo stock = service.parseStockInfo(map);
		//System.out.println("" + stock.getSymbol());
		//List<StockObj> list = stock.getChartlist();
		//double differ = average192021(stock);
		//System.out.println("differ=" + differ);
		//step1:
		//printTime(list,filePath);
		//step2:
		//printClosePrice(list,filePath);
		//step3:
		//List<StockObj> list_after = handle20(list);
		/*List<StockObj> list_after = handle30(list);
		for(StockObj obj : list_after){
			//System.out.println(obj.getTime()+"|" + obj.getClosePrice());
			//System.out.println(obj.getTime());
			StockUtil.writeLinesAndClose(filePath,obj.getClosePrice());
		}*/
		
	}
	
	
	private static double  getDiffer(StockService service,String token,String symbol){
		String end = String.valueOf(System.currentTimeMillis());
		String begin = String.valueOf(System.currentTimeMillis() - 40*24*60*60*1000l);
		String stockInfo = service.getInfo(token,symbol,begin,end);
		Map map = JsonUtils.fromJson(stockInfo, Map.class);
		StockInfo stock = service.parseStockInfo(map);
		return average192021(stock);
		
	}
	private static double average192021(StockInfo stockInfo){
		double temp_double = 0.0d;
		List<StockObj> list = stockInfo.getChartlist();
		int size = list.size();
		int index19 = size-20;
		int index20 = size - 21;
		int index21 = size - 22;
		if(index21>0){
			double closePrice19 = list.get(index19).getClosePrice();
			double closePrice20 = list.get(index20).getClosePrice();
			double closePrice21 = list.get(index21).getClosePrice();
			double closePriceToday = list.get(size-1).getClosePrice();
			double average = (closePrice19 + closePrice20 + closePrice21)/3;
			String priceStr = formateDouble(average);
		    temp_double = Double.parseDouble(priceStr);	
		    temp_double = closePriceToday - average;
		    temp_double = Double.parseDouble(formateDouble(temp_double/average));
		}
		
		return temp_double;
	}
	
	private static String formateDouble(double doubleNumber){
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.0000");  
		String priceStr = df.format(doubleNumber);
		return priceStr;
	}
	private static void printTime(List<StockObj> list,String filePaht) throws Exception{
		for(StockObj obj : list){
			StockUtil.writeLinesAndClose(filePaht,obj.getTime());
		}
	}
	
	private static void printClosePrice(List<StockObj> list,String filePath)  throws Exception{
		for(StockObj obj : list){
			StockUtil.writeLinesAndClose(filePath,obj.getClosePrice());
		}
	}
	
	public static List<StockObj> handle30(List<StockObj> list){
		List<StockObj> returnList = new ArrayList<StockObj>();
		StockObj temp = null;
		if(list != null && list.size()>0){
			int size = list.size();
			
			for(int i = 0;i<size;i++){
				int befor29 = i-29;
				int befor30 = i-30;
				int befor31 = i-31;
				StockObj obj = list.get(i);
				if(befor31<0){
					temp = new StockObj();
					temp.setTime(obj.getTime());
					temp.setClosePrice(0d);
					returnList.add(temp);
				}else{
					StockObj obj19 = list.get(befor29);
					StockObj obj20 = list.get(befor30);
					StockObj obj21 = list.get(befor31);
					temp = new StockObj();
					temp.setClosePrice((obj19.getClosePrice()+obj20.getClosePrice()+obj21.getClosePrice())/3);
					temp.setTime(obj.getTime());
					returnList.add(temp);
				}
			}
			
		}
		return returnList;
	}
	
	
	public static List<StockObj> handle20(List<StockObj> list){
		List<StockObj> returnList = new ArrayList<StockObj>();
		StockObj temp = null;
		if(list != null && list.size()>0){
			int size = list.size();
			
			for(int i = 0;i<size;i++){
				int befor19 = i-19;
				int befor20 = i-20;
				int befor21 = i-21;
				StockObj obj = list.get(i);
				if(befor21<0){
					temp = new StockObj();
					temp.setTime(obj.getTime());
					temp.setClosePrice(0d);
					returnList.add(temp);
				}else{
					StockObj obj19 = list.get(befor19);
					StockObj obj20 = list.get(befor20);
					StockObj obj21 = list.get(befor21);
					temp = new StockObj();
					temp.setClosePrice((obj19.getClosePrice()+obj20.getClosePrice()+obj21.getClosePrice())/3);
					temp.setTime(obj.getTime());
					returnList.add(temp);
				}
			}
			
		}
		return returnList;
	}
	
}
