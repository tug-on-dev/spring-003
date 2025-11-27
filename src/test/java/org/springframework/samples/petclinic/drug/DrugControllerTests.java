/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.drug;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link DrugController}
 */
@WebMvcTest(DrugController.class)
@DisabledInNativeImage
@DisabledInAotMode
class DrugControllerTests {

	private static final int TEST_DRUG_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DrugRepository drugs;

	private Drug amoxicillin() {
		Drug drug = new Drug();
		drug.setId(TEST_DRUG_ID);
		drug.setName("Amoxicillin");
		drug.setPrice(25.99);
		return drug;
	}

	@BeforeEach
	void setup() {
		Drug drug = amoxicillin();
		given(this.drugs.findById(TEST_DRUG_ID)).willReturn(Optional.of(drug));
		given(this.drugs.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(drug)));
	}

	@Test
	void testListDrugs() throws Exception {
		mockMvc.perform(get("/drugs"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listDrugs"))
			.andExpect(view().name("drugs/drugsList"));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/drugs/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("drug"))
			.andExpect(view().name("drugs/createOrUpdateDrugForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/drugs/new").param("name", "Carprofen").param("price", "45.50"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/drugs/new").param("name", "").param("price", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("drug"))
			.andExpect(view().name("drugs/createOrUpdateDrugForm"));
	}

	@Test
	void testProcessCreationFormPriceTooHigh() throws Exception {
		mockMvc.perform(post("/drugs/new").param("name", "Expensive Drug").param("price", "1000"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("drug"))
			.andExpect(model().attributeHasFieldErrors("drug", "price"))
			.andExpect(view().name("drugs/createOrUpdateDrugForm"));
	}

	@Test
	void testProcessCreationFormPriceNegative() throws Exception {
		mockMvc.perform(post("/drugs/new").param("name", "Cheap Drug").param("price", "-10"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("drug"))
			.andExpect(model().attributeHasFieldErrors("drug", "price"))
			.andExpect(view().name("drugs/createOrUpdateDrugForm"));
	}

	@Test
	void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/drugs/{drugId}/edit", TEST_DRUG_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("drug"))
			.andExpect(model().attribute("drug", hasProperty("name", is("Amoxicillin"))))
			.andExpect(model().attribute("drug", hasProperty("price", is(25.99))))
			.andExpect(view().name("drugs/createOrUpdateDrugForm"));
	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc.perform(post("/drugs/{drugId}/edit", TEST_DRUG_ID).param("name", "Amoxicillin").param("price", "30.00"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/drugs"));
	}

	@Test
	void testProcessUpdateFormHasErrors() throws Exception {
		mockMvc.perform(post("/drugs/{drugId}/edit", TEST_DRUG_ID).param("name", "Amoxicillin").param("price", "1500"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("drug"))
			.andExpect(model().attributeHasFieldErrors("drug", "price"))
			.andExpect(view().name("drugs/createOrUpdateDrugForm"));
	}

	@Test
	void testDeleteDrug() throws Exception {
		mockMvc.perform(get("/drugs/{drugId}/delete", TEST_DRUG_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/drugs"));
	}

}
