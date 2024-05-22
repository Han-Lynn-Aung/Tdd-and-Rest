package example.cashcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
class CashCardController {

    @Autowired
    private CashCardRepository cashCardRepository;


    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = this.cashCardRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(
                        Sort.by(Sort.Direction.ASC,"amount")
                )));
        return ResponseEntity.ok(
                page.getContent()
        );
    }


    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        System.out.println(cashCardOptional);
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb) {

        CashCard saveCashCard = this.cashCardRepository.save(cashCard);
        URI location = ucb.path("/cashcards/{id}").buildAndExpand(saveCashCard.id()).toUri();

        return ResponseEntity.created(location).build();
    }


}