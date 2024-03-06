package com.aaa.automation.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aaa.automation.dto.FilesDTO;
import com.aaa.automation.dto.HeadersDTO;
import com.aaa.automation.dto.ResponseDTO;
import com.opencsv.CSVReader;

@Service
public class FileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

	public void saveFiles(MultipartFile inputFile, MultipartFile dbResults) {

		try {
			// Create the file on the server
			// Get the file and save it to the specified location
			byte[] bytes = inputFile.getBytes();
			Path path = Paths.get("files/inputFiles" + File.separator + inputFile.getOriginalFilename());
			Files.deleteIfExists(path); // Delete existing file if it exists
			Files.write(path, bytes);

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

		try {
			byte[] bytes = dbResults.getBytes();
			Path path = Paths.get("files/dbResults" + File.separator + dbResults.getOriginalFilename());
			Files.write(path, bytes);

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public FilesDTO getAllFiles() {
		FilesDTO filesDTO = new FilesDTO();
		List<String> inputFileList = getFiles("files/inputFiles");
		filesDTO.setInputFiles(inputFileList);
		List<String> dbResultsList = getFiles("files/dbResults");
		filesDTO.setDbResults(dbResultsList);
		return filesDTO;
	}

	public HeadersDTO getHeaders(String inputFile, String dbResult) {
		HeadersDTO headersDTO = new HeadersDTO();
		List<String> inoutFileHeaders = returnHeaders("files/inputFiles/" + inputFile);
		headersDTO.setInputFileHeaders(inoutFileHeaders);
		List<String> dbResultsHeaders = returnHeaders("files/dbResults/" + dbResult);
		headersDTO.setDbResultHeaders(dbResultsHeaders);
		return headersDTO;
	}

	public List<String> getAvialbleValues(String inputFile, String header, boolean isInput) {

		List<String> inputFileHeaders = null;
		if (isInput) {
			inputFileHeaders = returnValues("files/inputFiles/" + inputFile, header);
		} else {
			inputFileHeaders = returnValues("files/dbResults/" + inputFile, header);
		}

		inputFileHeaders = inputFileHeaders.stream().distinct().toList();
		return inputFileHeaders;
	}

	public List<String> getResults(String inputFile, String srcHeader, String value, String destHeader,
			boolean isInput) {

		List<String> results = null;
		if (isInput) {
			results = getMatchedRaws("files/inputFiles/" + inputFile, srcHeader, value, destHeader);
		} else {
			results = getMatchedRaws("files/dbResults/" + inputFile, srcHeader, value, destHeader);
		}

		return results;
	}

	private List<String> getFiles(String path) {
		List<String> fileList = new ArrayList<>();
		File directory = new File(path);
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					fileList.add(file.getName());
				}
			}
		}
		return fileList;

	}

	public List<String> returnHeaders(String fileName) {
		List<String> headers = new ArrayList<>();
		// Logic for xls file format
		if (fileName.contains("xls") && !fileName.contains("xlsx")) {
			HSSFWorkbook workbook;
			try {

				FileInputStream fis = new FileInputStream(fileName);
				workbook = new HSSFWorkbook(fis);

				HSSFSheet worksheet = workbook.getSheetAt(0);
				Row firstRow = worksheet.getRow(0);

				Iterator<Cell> cellIterator = firstRow.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					headers.add(cell.getStringCellValue());
				}
				return headers;
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			// Logic for xlsx file format
		} else if (fileName.contains("xlsx")) {
			XSSFWorkbook workbook;
			try {

				FileInputStream fis = new FileInputStream(fileName);
				workbook = new XSSFWorkbook(fis);

				XSSFSheet worksheet = workbook.getSheetAt(0);

				Row firstRow = worksheet.getRow(0);

				Iterator<Cell> cellIterator = firstRow.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					headers.add(cell.getStringCellValue());
				}
				return headers;
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			// Logic for csv file format
		} else if (fileName.contains("csv")) {
			BufferedReader br = null;
			try {

				br = new BufferedReader(new FileReader(fileName));
				String line;

				while ((line = br.readLine()) != null) {

					if (line != null) {
						// Split the first line by comma to get the headers
						br.close();
						return Arrays.asList(line.split(","));
					}

				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}

		return headers;
	}

	public List<String> returnValues(String fileName, String header) {
		List<String> headers = new ArrayList<>();
		// Logic for xls file format
		if (fileName.contains("xls") && !fileName.contains("xlsx")) {
			HSSFWorkbook workbook;
			try {

				FileInputStream fis = new FileInputStream(fileName);
				workbook = new HSSFWorkbook(fis);

				HSSFSheet worksheet = workbook.getSheetAt(0);
				Row firstRow = worksheet.getRow(0);

				Iterator<Cell> cellIterator = firstRow.cellIterator();
				int matchFieldCount = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell.getStringCellValue().equalsIgnoreCase(header)) {
						break;
					}
					matchFieldCount++;
				}
				for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

					HSSFRow row = worksheet.getRow(i);

					headers.add(row.getCell(matchFieldCount).getStringCellValue());

				}
				return headers;
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			// Logic for xlsx file format
		} else if (fileName.contains("xlsx")) {
			XSSFWorkbook workbook;
			try {

				FileInputStream fis = new FileInputStream(fileName);
				workbook = new XSSFWorkbook(fis);

				XSSFSheet worksheet = workbook.getSheetAt(0);

				Row firstRow = worksheet.getRow(0);

				Iterator<Cell> cellIterator = firstRow.cellIterator();
				int matchFieldCount = 0;

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell.getStringCellValue().equalsIgnoreCase(header)) {
						break;
					}
					matchFieldCount++;
				}
				for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

					XSSFRow row = worksheet.getRow(i);

					try {
						headers.add(row.getCell(matchFieldCount).getStringCellValue());
					} catch (Exception e) {
						double val = row.getCell(matchFieldCount).getNumericCellValue();
						int valInt = (int) val;
						headers.add(Integer.toString(valInt));
					}

				}
				return headers;
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			// Logic for csv file format
		} else if (fileName.contains("csv")) {
			BufferedReader br = null;
			try {

				br = new BufferedReader(new FileReader(fileName));
				String line;
				List<String> headerList = null;
				while ((line = br.readLine()) != null) {

					if (line != null) {
						// Split the first line by comma to get the headers
						br.close();
						headerList = Arrays.asList(line.split(","));
						break;
					}

				}
				int headerPos = 0;

				for (int i = 0; i < headerList.size(); i++) {
					if (headerList.get(i).equalsIgnoreCase(header)) {
						headerPos = i;

					}
				}
				br = new BufferedReader(new FileReader(fileName));

				boolean fistLine = true;

				try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
					String[] nextLine;
					while ((nextLine = reader.readNext()) != null) {
						if (!fistLine) {
							if (nextLine[headerPos].contains(",")) {
								headers.add("Comma seperated values");
							} else {
								headers.add(nextLine[headerPos]);
							}

						}
						fistLine = false;

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}

		return headers;
	}

	public ResponseDTO compare(List<String> inputResults, List<String> inputSeconderyResults, List<String> dbResults,
			List<String> dbSeconderyResults) {
		ResponseDTO responseDTO = new ResponseDTO();
		boolean isMatched = false;
		List<String> finalInputList = new ArrayList<String>();
		finalInputList.addAll(inputResults);
		finalInputList.addAll(inputSeconderyResults);
		
		List<String> finalDbList = new ArrayList<String>();
		finalDbList.addAll(dbResults);
		finalDbList.addAll(dbSeconderyResults);
		
		Collections.sort(finalInputList);
		Collections.sort(finalDbList);

		isMatched = finalInputList.equals(finalDbList);
		responseDTO.setMatched(isMatched);
		if (!isMatched) {
			List<String> inputResultsCopy = new ArrayList<>(finalInputList);
			inputResultsCopy.removeAll(finalDbList);
			responseDTO.setOnlyInInputResults(inputResultsCopy);
			inputResults.retainAll(inputResultsCopy);
			responseDTO.setInInputPrimaryList(inputResults);
			inputSeconderyResults.retainAll(inputResultsCopy);
			responseDTO.setInInputSecondaryList(inputSeconderyResults);
			List<String> dbResultsCopy = new ArrayList<>(finalDbList);
			dbResultsCopy.removeAll(finalInputList);
			dbResults.retainAll(dbResultsCopy);
			responseDTO.setInDbPrimaryList(dbResults);
			dbSeconderyResults.retainAll(dbResultsCopy);
			responseDTO.setInDbSecondaryList(dbSeconderyResults);
			responseDTO.setOnlyInDbResults(dbResultsCopy);
		}

		return responseDTO;
	}

	public List<String> getMatchedRaws(String fileName, String srcHeader, String value, String destHeader) {
		List<String> headers = new ArrayList<>();
		// Logic for xls file format
		if (fileName.contains("xls") && !fileName.contains("xlsx")) {
			HSSFWorkbook workbook;
			try {

				FileInputStream fis = new FileInputStream(fileName);
				workbook = new HSSFWorkbook(fis);

				HSSFSheet worksheet = workbook.getSheetAt(0);
				Row firstRow = worksheet.getRow(0);

				Iterator<Cell> cellIterator = firstRow.cellIterator();
				int matchFieldCount = 0;
				int matchedFieldCount = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell.getStringCellValue().equalsIgnoreCase(srcHeader)) {
						break;
					}
					matchFieldCount++;
				}
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell.getStringCellValue().equalsIgnoreCase(destHeader)) {
						break;
					}
					matchedFieldCount++;
				}
				for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

					HSSFRow row = worksheet.getRow(i);
					if (value.equalsIgnoreCase(row.getCell(matchFieldCount).getStringCellValue())) {
						try {
							headers.add(row.getCell(matchedFieldCount).getStringCellValue());
						} catch (Exception e) {
							double val = row.getCell(matchedFieldCount).getNumericCellValue();
							int valInt = (int) val;
							headers.add(Integer.toString(valInt));
						}
					}

				}
				return headers;
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			// Logic for xlsx file format
		} else if (fileName.contains("xlsx")) {
			XSSFWorkbook workbook;
			try {

				FileInputStream fis = new FileInputStream(fileName);
				workbook = new XSSFWorkbook(fis);

				XSSFSheet worksheet = workbook.getSheetAt(0);

				Row firstRow = worksheet.getRow(0);

				Iterator<Cell> cellIterator = firstRow.cellIterator();
				Iterator<Cell> cellIterator2 = firstRow.cellIterator();
				int matchFieldCount = 0;
				int matchedFieldCount = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell.getStringCellValue().equalsIgnoreCase(srcHeader)) {
						break;
					}
					matchFieldCount++;
				}
				while (cellIterator2.hasNext()) {
					Cell cell = cellIterator2.next();
					if (cell.getStringCellValue().equalsIgnoreCase(destHeader)) {
						break;
					}
					matchedFieldCount++;
				}
				for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

					XSSFRow row = worksheet.getRow(i);
					if (value.equalsIgnoreCase(row.getCell(matchFieldCount).getStringCellValue())) {
						try {
							headers.add(row.getCell(matchedFieldCount).getStringCellValue());
						} catch (Exception e) {
							double val = row.getCell(matchedFieldCount).getNumericCellValue();
							int valInt = (int) val;
							headers.add(Integer.toString(valInt));
						}

					}

				}
				return headers;
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
			// Logic for csv file format
		} else if (fileName.contains("csv")) {
			BufferedReader br = null;
			try {

				br = new BufferedReader(new FileReader(fileName));
				String line;
				List<String> headerList = null;
				while ((line = br.readLine()) != null) {

					if (line != null) {
						// Split the first line by comma to get the headers
						br.close();
						headerList = Arrays.asList(line.split(","));
						break;
					}

				}
				int srcHeaderPos = 0;
				int destHeaderPos = 0;

				for (int i = 0; i < headerList.size(); i++) {
					if (headerList.get(i).equalsIgnoreCase(srcHeader)) {
						srcHeaderPos = i;

					}
					if (headerList.get(i).equalsIgnoreCase(destHeader)) {
						destHeaderPos = i;

					}
				}
				br = new BufferedReader(new FileReader(fileName));
				/*
				 * while ((line = br.readLine()) != null) {
				 * 
				 * if (line != null) { // Split the first line by comma to get the headers
				 * 
				 * headerList= Arrays.asList(line.split(","));
				 * if(headerList.get(srcHeaderPos).equalsIgnoreCase(value)) {
				 * headers.add(headerList.get(destHeaderPos)); } }
				 * 
				 * }
				 */
				try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
					String[] nextLine;
					while ((nextLine = reader.readNext()) != null) {
						if (nextLine[srcHeaderPos].equalsIgnoreCase(value)) {
							headers.add(nextLine[destHeaderPos]);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
		if (headers.size() == 1 && headers.get(0).indexOf(",") > 0) {
			headers = Arrays.asList(headers.get(0).split(","));
		}
		Collections.sort(headers);
		return headers;
	}
}
