package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.request.PersonDTO;
import one.digitalinnovation.personapi.dto.responses.MessageResponseDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.dto.mapper.PersonMapper;
import one.digitalinnovation.personapi.exception.PersonNotFoundException;
import one.digitalinnovation.personapi.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    private final PersonMapper personMapper = PersonMapper.INSTANCE;

    @Autowired
    public PersonService(PersonRepository personRepository){
        this.personRepository = personRepository;
    }


    public MessageResponseDTO createPerson(PersonDTO personDTO) {
        Person personToSave = personMapper.toModel(personDTO);
        Person savedPerson = personRepository.save(personToSave);

        MessageResponseDTO messageResponse = MessageResponseDTO.builder()
                .message("Person created with ID " + savedPerson.getId())
                .build();

        return messageResponse;
    }


    public List<PersonDTO> listAll() {
        List<Person> allPeople = personRepository.findAll();
        return allPeople
                .stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PersonDTO findById(Long id) throws PersonNotFoundException {
        Person person = verifyIfExistis(id);
        return personMapper.toDTO(person);
    }

    public void delete(Long id) throws PersonNotFoundException{
        verifyIfExistis(id);
        personRepository.deleteById(id);
    }

    private Person verifyIfExistis(Long id) throws PersonNotFoundException{
        return personRepository.findById(id)
                .orElseThrow(()-> new PersonNotFoundException(id)) ;
    }

    public MessageResponseDTO updateById(Long id, PersonDTO personDTO) throws PersonNotFoundException{
        verifyIfExistis(id);

        Person personToUpdate = personMapper.toModel(personDTO);
        Person updatePerson = personRepository.save(personToUpdate);

        return createMessageResponse(updatePerson.getId(), "Update person with ID ");
    }

    private MessageResponseDTO createMessageResponse(Long id, String s){
        return MessageResponseDTO.builder()
                .message(s + id)
                .build();
    }
}
