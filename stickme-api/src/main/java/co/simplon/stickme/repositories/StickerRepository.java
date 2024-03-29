package co.simplon.stickme.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import co.simplon.stickme.dtos.StickerAdminItem;
import co.simplon.stickme.dtos.StickerDetail;
import co.simplon.stickme.dtos.StickerForUpdate;
import co.simplon.stickme.dtos.StickerItem;
import co.simplon.stickme.entities.Sticker;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    // Exercices : derived queries / query methods

    Sticker findByName(String name);

    Sticker findByNameAndAspectName(String stickerName, String aspectName);

    Collection<Sticker> findTop2ByOrderByPriceDesc();
    // Do not change:

    StickerForUpdate findProjectedById(Long id);

    StickerDetail findProjectedDetailById(Long id);

    Collection<StickerItem> findAllProjectedByOrderByPrice();

    Collection<StickerAdminItem> findAllProjectedByOrderByDateAddedDescName();
}
