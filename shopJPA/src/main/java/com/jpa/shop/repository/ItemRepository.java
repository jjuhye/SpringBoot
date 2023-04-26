package com.jpa.shop.repository;


import com.jpa.shop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);  // 새로운 item 전체변경 : name 수정 안하면 name => null
            // 수정하는 방식이 2가지
            // merge 랑 em.find()
            //Item updateItem = em.find(Item.class,item.getId());
           // em.persist(updateItem);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
