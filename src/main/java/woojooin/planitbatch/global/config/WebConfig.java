package woojooin.planitbatch.global.config;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] {AppConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] {ServletConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {
			"/"
		};
	}

	// POST body 문자 인코딩 필터 설정 - UTF-8 설정
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();

		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		return new Filter[] {characterEncodingFilter};
	}
}

