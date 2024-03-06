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
public class HeadersDTO {
	
	List<String> inputFileHeaders;
	List<String> dbResultHeaders;

}
