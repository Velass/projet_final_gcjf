package com.diginamic.apiback;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.diginamic.apiback.services.TraitementNuitService;

@SpringBootTest
class ApiBackApplicationTests {

	@SpyBean
	private TraitementNuitService traitementNuitService;

	@Test
	void contextLoads() {
		traitementNuitService.launchTraitementNuit();
	}

}
