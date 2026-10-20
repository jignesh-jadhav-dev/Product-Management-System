package com.product_order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.product_order.dto.AddressDTO;
import com.product_order.entity.Address;
import com.product_order.entity.User;
import com.product_order.excception.ResourceNotFoundException;
import com.product_order.repository.AddressRepository;
import com.product_order.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	public AddressDTO addAddress(Long userId, AddressDTO dto) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		Address address = new Address();
		address.setStreet(dto.getStreet());
		address.setCity(dto.getCity());
		address.setState(dto.getState());
		address.setPincode(dto.getPincode());
		address.setUser(user);
		Address saved = addressRepository.save(address);

		return new AddressDTO(saved.getId(), saved.getStreet(), saved.getCity(), saved.getState(), saved.getPincode());
	}

	public List<AddressDTO> getAddressesByUser(Long userId) {
		return addressRepository.findByUserId(userId).stream()
				.map(a -> new AddressDTO(a.getId(), a.getStreet(), a.getCity(), a.getState(), a.getPincode()))
				.collect(Collectors.toList());
	}
}