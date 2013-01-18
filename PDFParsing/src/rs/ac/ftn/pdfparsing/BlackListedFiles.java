package rs.ac.ftn.pdfparsing;

import java.io.File;
import java.util.HashMap;

public class BlackListedFiles {	
	static BlackListedFiles instance = null;
	String[] blacklistedPaths = new String[] {
			//doesn't get parsed properly
			"2010/rmit/141.txt",
			"2010/rmit/067.txt",
			"2010/sw/076.txt",
			"2010/zp/066.txt",
			"2011/vi/073.txt",
			"2012/rmit/480.txt",
			"2007/pi/070.txt",
			
			//abstract isn't named at all
			"2010/vi/103.txt",
			"2006/internet/020.txt",
			
			//"abstract" contains serbian text
			"2006/networks/166.txt",
			"2006/infosys/037.txt",
			"2006/applied/163.txt",
			"2012/is/303.txt",
			
			//no abstract
			"2006/infosys/246.txt",
			
			//TODO: why aren't these working?,
			"2010/pi/205.txt",
			"2007/pi/079.txt",
	};
	HashMap<String, Boolean> blackListedMap = new HashMap<String, Boolean>();
	
	
	String [] noSerbianFilePaths = new String[] {
			"2010/rmit/126.txt",
			"2010/rmit/110.txt",
			"2010/rmit/091.txt",
			"2010/esoc/121.txt",
			"2010/esoc/084.txt",
			"2010/zp/022.txt",
			"2010/zp/134.txt",
			"2011/sec/304.txt",
			"2011/rmit/279.txt",
			"2011/is/132.txt",
			"2011/sw/178.txt",
			"2011/pi/159.txt",
			"2011/pi/135.txt",
			"2011/pi/262.txt",
			"2011/pi/134.txt",
			"2011/esoc/190.txt",
			"2011/hw/096.txt",
			"2009/rmit/144.txt",
			"2009/vi/095.txt",
			"2009/vi/044.txt",
			"2009/vi/115.txt",
			"2009/vi/116.txt",
			"2009/sw/088.txt",
			"2009/pi/175.txt",
			"2009/esoc/097.txt",
			"2009/esoc/170.txt",
			"2009/esoc/134.txt",
			"2009/zp/101.txt",
			"2009/hw/133.txt",
			"2006/networks/007.txt",
			"2006/internet/052.txt",
			"2006/applied/088.txt",
			"2006/applied/014.txt",
			"2006/software/181.txt",
			"2006/software/032.txt",
			"2012/rmit/363.txt",
			"2012/pi/511.txt",
			"2012/pi/516.txt",
			"2012/esoc/321.txt",
			"2012/esoc/334.txt",
			"2012/esoc/544.txt",
			"2012/zp/322.txt",
			"2008/rmit/164.txt",
			"2008/is/172.txt",
			"2008/sw/147.txt",
			"2008/esoc/040.txt",
			"2008/esoc/087.txt",
			"2007/rs/107.txt",
			"2007/pi/019.txt",
			"2007/pi/012.txt",
			"2007/pi/020.txt",
			"2007/pi/049.txt",
			"2007/esoc/191.txt",
			"2007/esoc/166.txt",
	};
	HashMap<String, Boolean> noSerbianMap = new HashMap<String, Boolean>();
	
	String [] noEnglishFilePaths = new String[] {
			"2010/zp/027.txt",
			"2011/zp/199.txt",
			"2009/is/183.txt",
			"2007/rmit/175.txt",			
			
			//TODO: spells English abstract as abstrakt
			"2011/pi/247.txt",
			"2006/software/110.txt",
			"2006/hardware/010.txt",
			"2012/sec/444.txt",
			"2012/esoc/501.txt",
			"2012/esoc/501.txt",
			"2008/esoc/128.txt",
			"2007/esoc/199.txt",
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
	
	private String getRelativeFilePath(String absoluteFilePath) {
		return absoluteFilePath.split("data/")[1];
	}
	
	public boolean isBlackListed(String filePath) {
		String relativeFilePath = getRelativeFilePath(filePath);
		return blackListedMap.containsKey(relativeFilePath) && blackListedMap.get(relativeFilePath);
	}
	
	public boolean hasNoSerbian(String filePath) {
		String relativeFilePath = getRelativeFilePath(filePath);
		return noSerbianMap.containsKey(relativeFilePath) && noSerbianMap.get(relativeFilePath);
	}
	
	public boolean hasNoEnglish(String filePath) {
		String relativeFilePath = getRelativeFilePath(filePath);
		return noEnglishMap.containsKey(relativeFilePath) && noEnglishMap.get(relativeFilePath);
	}
}
