package example.cashcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/cashcards")
class CashCardController {

    @Autowired
    private CashCardRepository cashCardRepository;


    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(
                                Sort.by(Sort.Direction.ASC, "amount")
                        )));
        return ResponseEntity.ok(
                page.getContent()
        );
    }


    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb, Principal principal) {

        CashCard cashCardWithOwner = new CashCard(null, cashCard.amount(), principal.getName());
        CashCard saveCashCard = this.cashCardRepository.save(cashCard);
        URI location = ucb.path("/cashcards/{id}").buildAndExpand(saveCashCard.id()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{requestId}")
    private ResponseEntity<Void> updateCashCard(@PathVariable Long requestId, @RequestBody CashCard cashCardUpdate, Principal principal) {

        CashCard cashCard = getCashCard(requestId, principal);
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private CashCard getCashCard(Long requestId, Principal principal) {
        return this.cashCardRepository.findByIdAndOwner(requestId, principal.getName());
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (!cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            return ResponseEntity.notFound().build();
        }
        cashCardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}