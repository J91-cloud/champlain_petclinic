package com.petclinic.vet.servicelayer;


import com.petclinic.vet.dataaccesslayer.Photo;
import com.petclinic.vet.dataaccesslayer.PhotoRepository;
import com.petclinic.vet.dataaccesslayer.badges.BadgeTitle;
import com.petclinic.vet.exceptions.InvalidInputException;
import com.petclinic.vet.exceptions.NotFoundException;
import com.petclinic.vet.presentationlayer.PhotoResponseDTO;
import com.petclinic.vet.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.io.IOException;




@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;


    @Override
    public Mono<Resource> getPhotoByVetId(String vetId) {
        return photoRepository.findByVetId(vetId)
                .switchIfEmpty(Mono.error(new NotFoundException("Photo for vet " + vetId + " does not exist.")))
                .map(img -> {
                    ByteArrayResource resource = new ByteArrayResource(img.getData());


                    return resource;
                });
    }


    @Override
    public Mono<PhotoResponseDTO> getDefaultPhotoByVetId(String vetId) {
        return photoRepository.findByVetId(vetId)
                .switchIfEmpty(Mono.error(new NotFoundException("vetId not found: " + vetId)))
                .map(EntityDtoUtil::toPhotoResponseDTO);
    }


    @Override
    public Mono<Resource> insertPhotoOfVet(String vetId, String photoName, Mono<Resource> photo) {
        return photo
                .map(p -> EntityDtoUtil.toPhotoEntity(vetId, photoName, p))
                .flatMap(photoRepository::save)
                .map(img -> {
                    // Create a Resource from the photo's InputStream
                    ByteArrayResource resource = new ByteArrayResource(img.getData());
                    //log.debug("Picture byte array in vet-service toServiceImpl" + resource);


                    return resource;
                });
    }


    @Override
    public Mono<Resource> updatePhotoByVetId(String vetId, String photoName, Mono<Resource> photo) {
        return photoRepository.findByVetId(vetId)
                .switchIfEmpty(Mono.error(new NotFoundException("Photo for vet " + vetId + " does not exist.")))
                .flatMap(existingPhoto -> photo.map(resource -> {
                            Photo updatedPhoto = EntityDtoUtil.toPhotoEntity(vetId, photoName, resource);
                            updatedPhoto.setId(existingPhoto.getId());
                            return updatedPhoto;
                        })
                        .flatMap(updatedPhoto -> {
                            return photoRepository.save(updatedPhoto)
                                    .map(savedPhoto -> {
                                        ByteArrayResource savedResource = new ByteArrayResource(savedPhoto.getData());
                                        return savedResource;
                                    });
                        }));
    }
 /*   @Override
    public Mono<Resource> insertPhotoOfVet(String vetId, String photoName, MultipartFile photo) {
        return Mono.fromCallable(() -> {
                    Photo photoEntity = new Photo();
                    photoEntity.setVetId(vetId);
                    photoEntity.setFilename(photoName);
                    photoEntity.setImgType(photo.getContentType());
                    photoEntity.setData(photo.getBytes());
                    return photoEntity;
                })
                .flatMap(photoRepository::save)
                .map(savedPhoto -> new ByteArrayResource(savedPhoto.getData()));
    }
*/

}
