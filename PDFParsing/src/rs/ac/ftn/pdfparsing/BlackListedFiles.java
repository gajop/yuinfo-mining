package rs.ac.ftn.pdfparsing;

import java.util.HashMap;

public class BlackListedFiles {
	static BlackListedFiles instance = null;
	String[] blacklistedPaths = new String[] {
			//doesn't get parsed properly
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/rmit/141.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/rmit/067.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/sw/076.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/zp/066.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/vi/073.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/rmit/480.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/pi/070.txt",
			
			//abstract isn't named at all
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/vi/103.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/internet/020.txt",
			
			//"abstract" contains serbian text
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/networks/166.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/infosys/037.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/applied/163.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/is/303.txt",
			
			//no abstract
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/infosys/246.txt",
			
			//TODO: why aren't these working?,
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/pi/205.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/pi/079.txt",
	};
	HashMap<String, Boolean> blackListedMap = new HashMap<String, Boolean>();
	
	
	String [] noSerbianFilePaths = new String[] {
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/rmit/126.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/rmit/110.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/rmit/091.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/esoc/121.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/esoc/084.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/zp/022.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/zp/134.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/sec/304.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/rmit/279.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/is/132.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/sw/178.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/pi/159.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/pi/135.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/pi/262.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/pi/134.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/esoc/190.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/hw/096.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/rmit/144.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/vi/095.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/vi/044.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/vi/115.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/vi/116.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/sw/088.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/pi/175.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/esoc/097.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/esoc/170.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/esoc/134.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/zp/101.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/hw/133.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/networks/007.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/internet/052.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/applied/088.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/applied/014.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/software/181.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/software/032.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/rmit/363.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/pi/511.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/pi/516.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/esoc/321.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/esoc/334.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/esoc/544.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/zp/322.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2008/rmit/164.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2008/is/172.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2008/sw/147.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2008/esoc/040.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2008/esoc/087.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/rs/107.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/pi/019.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/pi/012.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/pi/020.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/pi/049.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/esoc/191.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/esoc/166.txt",
	};
	HashMap<String, Boolean> noSerbianMap = new HashMap<String, Boolean>();
	
	String [] noEnglishFilePaths = new String[] {
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2010/zp/027.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/zp/199.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2009/is/183.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/rmit/175.txt",			
			
			//TODO: spells English abstract as abstrakt
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2011/pi/247.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/software/110.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2006/hardware/010.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/sec/444.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/esoc/501.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2012/esoc/501.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2008/esoc/128.txt",
			"/home/gajop/work/yuinfo-mining/PDFParsing/../data/2007/esoc/199.txt",
	};
	HashMap<String, Boolean> noEnglishMap = new HashMap<String, Boolean>();
	
	private BlackListedFiles() {
		for (String blacklistedPath : blacklistedPaths) {
			blackListedMap.put(blacklistedPath, true);
		}
		for (String noSerbianFilePath : noSerbianFilePaths) {
			noSerbianMap.put(noSerbianFilePath, true);
		}
		for (String noEnglishFilePath : noEnglishFilePaths) {
			noEnglishMap.put(noEnglishFilePath, true);
		}
	}
	
	public static BlackListedFiles getInstance() {
		if (instance == null) {
			instance = new BlackListedFiles();
		}
		return instance;
	}
	
	public boolean isBlackListed(String filePath) {
		return blackListedMap.containsKey(filePath) && blackListedMap.get(filePath);
	}
	
	public boolean hasNoSerbian(String filePath) {
		return noSerbianMap.containsKey(filePath) && noSerbianMap.get(filePath);
	}
	
	public boolean hasNoEnglish(String filePath) {
		return noEnglishMap.containsKey(filePath) && noEnglishMap.get(filePath);
	}
}
