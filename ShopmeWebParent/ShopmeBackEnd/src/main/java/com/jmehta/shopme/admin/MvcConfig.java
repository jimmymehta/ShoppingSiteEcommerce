package com.jmehta.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jmehta.shopme.admin.paging.PagingAndSortingArgumentResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// for exposing user images directory on web
		exposeDirectory(registry, "user-photos");

		// for exposing category images directory on web
		exposeDirectory(registry, "../category-images");

		// for exposing Brand images directory on web
		exposeDirectory(registry, "../brand-logos");
		
		// for exposing Product images directory on web
		exposeDirectory(registry, "../product-images");
		
		// for exposing Site logo directory on web
		exposeDirectory(registry, "../site-logo");

	}
	
	private void exposeDirectory(ResourceHandlerRegistry registry, String dirName) {
		
		Path dir = Paths.get(dirName);
		
		String fullPath = dir.toFile().getAbsolutePath();
		
		String logicalPath = dirName.replace("../", "") + "/**";
		
		registry.addResourceHandler(logicalPath).addResourceLocations("file:" + fullPath + "/");
		
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new PagingAndSortingArgumentResolver());
	}
	
	

}
