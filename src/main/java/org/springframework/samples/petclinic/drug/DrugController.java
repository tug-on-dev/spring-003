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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controller for managing drugs.
 *
 * @author PetClinic
 */
@Controller
class DrugController {

	private static final String VIEWS_DRUG_CREATE_OR_UPDATE_FORM = "drugs/createOrUpdateDrugForm";

	private final DrugRepository drugRepository;

	public DrugController(DrugRepository drugRepository) {
		this.drugRepository = drugRepository;
	}

	@ModelAttribute("drug")
	public Drug findDrug(@PathVariable(name = "drugId", required = false) Integer drugId) {
		return drugId == null ? new Drug()
				: this.drugRepository.findById(drugId)
					.orElseThrow(() -> new IllegalArgumentException(
							"Drug not found with id: " + drugId + ". Please ensure the ID is correct."));
	}

	@GetMapping("/drugs")
	public String listDrugs(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<Drug> paginated = findPaginated(page);
		return addPaginationModel(page, paginated, model);
	}

	private String addPaginationModel(int page, Page<Drug> paginated, Model model) {
		List<Drug> listDrugs = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listDrugs", listDrugs);
		return "drugs/drugsList";
	}

	private Page<Drug> findPaginated(int page) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return drugRepository.findAll(pageable);
	}

	@GetMapping("/drugs/new")
	public String initCreationForm() {
		return VIEWS_DRUG_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/drugs/new")
	public String processCreationForm(@Valid Drug drug, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the drug.");
			return VIEWS_DRUG_CREATE_OR_UPDATE_FORM;
		}

		this.drugRepository.save(drug);
		redirectAttributes.addFlashAttribute("message", "New Drug Created");
		return "redirect:/drugs";
	}

	@GetMapping("/drugs/{drugId}/edit")
	public String initUpdateForm() {
		return VIEWS_DRUG_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/drugs/{drugId}/edit")
	public String processUpdateForm(@Valid Drug drug, BindingResult result, @PathVariable("drugId") int drugId,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the drug.");
			return VIEWS_DRUG_CREATE_OR_UPDATE_FORM;
		}

		drug.setId(drugId);
		this.drugRepository.save(drug);
		redirectAttributes.addFlashAttribute("message", "Drug Updated Successfully");
		return "redirect:/drugs";
	}

	@GetMapping("/drugs/{drugId}/delete")
	public String deleteDrug(@PathVariable("drugId") int drugId, RedirectAttributes redirectAttributes) {
		Drug drug = this.drugRepository.findById(drugId)
			.orElseThrow(() -> new IllegalArgumentException(
					"Drug not found with id: " + drugId + ". Please ensure the ID is correct."));
		this.drugRepository.delete(drug);
		redirectAttributes.addFlashAttribute("message", "Drug Deleted Successfully");
		return "redirect:/drugs";
	}

}
