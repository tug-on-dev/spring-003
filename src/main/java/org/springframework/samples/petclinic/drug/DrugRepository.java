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

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for <code>Drug</code> domain objects.
 *
 * @author PetClinic
 */
public interface DrugRepository extends Repository<Drug, Integer> {

	/**
	 * Retrieve all {@link Drug}s from the data store in pages.
	 * @param pageable the pagination information
	 * @return a page of {@link Drug}s
	 */
	@Transactional(readOnly = true)
	Page<Drug> findAll(Pageable pageable);

	/**
	 * Retrieve a {@link Drug} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Drug} if found
	 */
	@Transactional(readOnly = true)
	Optional<Drug> findById(Integer id);

	/**
	 * Save a {@link Drug} to the data store, either inserting or updating it.
	 * @param drug the {@link Drug} to save
	 * @return the saved {@link Drug}
	 */
	Drug save(Drug drug);

	/**
	 * Delete a {@link Drug} from the data store.
	 * @param drug the {@link Drug} to delete
	 */
	void delete(Drug drug);

}
