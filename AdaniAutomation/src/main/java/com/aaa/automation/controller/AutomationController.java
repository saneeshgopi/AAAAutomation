package com.aaa.automation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aaa.automation.dto.FilesDTO;
import com.aaa.automation.dto.HeadersDTO;
import com.aaa.automation.dto.ResponseDTO;
import com.aaa.automation.service.FileService;

@RestController
@RequestMapping(path = "/automation")
@CrossOrigin(origins = "http://localhost:3000")
public class AutomationController {

	@Autowired
	FileService fileService;

	/**
	 * @param file
	 * @param customerId
	 * @return
	 */
	@PostMapping("/uploadFiles")
	public void uploadFile(@RequestParam("inputFile") MultipartFile inputFile,
			@RequestParam("dbResults") MultipartFile dbResults) throws Exception {

		fileService.saveFiles(inputFile, dbResults);
		// fileService.parseFiles(inputFile,dbResults );

	}

	@GetMapping("/getAllFiles")
	public FilesDTO getAllFiles() {
		return fileService.getAllFiles();
	}

	@GetMapping("/getHeaders")
	public HeadersDTO getHeaders(@RequestParam("inputFile") String inputFile,
			@RequestParam("dbResults") String dbResult) {
		return fileService.getHeaders(inputFile, dbResult);
	}

	@GetMapping("/getAvialbleValues")
	public List<String> getAvialbleValues(@RequestParam("inputFile") String inputFile,
			@RequestParam("header") String header, @RequestParam("isInput") boolean isInput) {
		return fileService.getAvialbleValues(inputFile, header, isInput);
	}

	@GetMapping("/getResults")
	public List<String> getResults(@RequestParam("inputFile") String inputFile,
			@RequestParam("srcHeader") String srcHeader, @RequestParam("value") String value,
			@RequestParam("destHeader") String destHeader, @RequestParam("isInput") boolean isInput) {
		return fileService.getResults(inputFile, srcHeader, value, destHeader, isInput);
	}

	@GetMapping("/compare")
	public ResponseDTO compare(@RequestParam("inputResults") List<String> inputResults,@RequestParam("inputSeconderyResults") List<String> inputSeconderyResults,
			@RequestParam("dbResults") List<String> dbResult,@RequestParam("dbSeconderyResults") List<String> dbSeconderyResults) {
		return fileService.compare(inputResults,inputSeconderyResults, dbResult,dbSeconderyResults);
	}
}
