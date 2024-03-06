package com.aaa.automation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDTO {
	
	List<String> onlyInInputResults;
	List<String> inInputPrimaryList;
	List<String> inInputSecondaryList;
	List<String> onlyInDbResults;
	List<String> inDbPrimaryList;
	List<String> inDbSecondaryList;
	boolean isMatched;

}
