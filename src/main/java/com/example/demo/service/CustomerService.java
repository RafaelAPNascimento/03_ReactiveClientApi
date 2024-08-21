package com.example.demo.service;

import com.example.demo.dto.CustomerDto;
import com.example.demo.mapper.EntityDtoMapper;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return this.customerRepository.findAll()
                .map(EntityDtoMapper::toDto);
    }

    public Mono<PageImpl<CustomerDto>> getAllCustomers(PageRequest pageRequest) {
        return this.customerRepository.findBy(pageRequest)
                .map(EntityDtoMapper::toDto)
                .collectList()
                .zipWith(customerRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));

    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.customerRepository.findById(id)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> mono) {
        return mono.map(EntityDtoMapper::toEntity)
                .flatMap(customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer id, Mono<CustomerDto> customerDtoMono) {
        return customerRepository.findById(id)      // find a customer by ID
                .flatMap(entity -> customerDtoMono)     // if it is found, we replace it by the updated customer (subscribe to this mono to get the dto)
                .map(EntityDtoMapper::toEntity)         // convert the dto into entity
                .doOnNext(entity -> entity.setId(id))       // as the entity might be missing the id (from request) we set it into it
                .flatMap(customerRepository::save)          // save the updated entity
                .map(EntityDtoMapper::toDto);           // map to dto as expected return type

    }

    public Mono<Boolean> deleteCustomerById(Integer id) {
        return customerRepository.deleteCustomerById(id);
    }
}
