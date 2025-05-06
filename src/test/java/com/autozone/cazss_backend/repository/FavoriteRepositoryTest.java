package com.autozone.cazss_backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.autozone.cazss_backend.CazssBackendApplication;
import com.autozone.cazss_backend.entity.*;
import com.autozone.cazss_backend.enumerator.EndpointMethodEnum;
import com.autozone.cazss_backend.enumerator.UserRoleEnum;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest(classes = CazssBackendApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FavoriteRepositoryTest {

  @Autowired FavoriteRepository favoriteRepository;

  @Autowired UserRepository userRepository;

  @Autowired EndpointsRepository endpointsRepository;

  @Autowired CategoryRepository categoryRepository;

  @Test
  @Order(1)
  public void givenFavoriteRepository_whenSaveAndRetrieveFavorite_thenOK() {
    // Configuración de la categoría
    String categoryName = "TEST";
    CategoryEntity category =
        categoryRepository
            .findByName(categoryName)
            .orElseGet(() -> categoryRepository.save(new CategoryEntity(categoryName, "#FFFFFF")));

    // Configuración del usuario
    String userEmail = "johndoe@autozone.com";
    UserEntity user =
        userRepository
            .findByEmail(userEmail)
            .orElseGet(
                () -> userRepository.save(new UserEntity(userEmail, true, UserRoleEnum.USER)));

    // Crear el endpoint
    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "FavoriteEndpoint",
                "Testing favorite",
                EndpointMethodEnum.GET,
                "https://test.favorite"));

    // Crear el FavoriteEntity
    FavoriteEntity.FavoriteId favoriteId =
        new FavoriteEntity.FavoriteId(user.getUserId(), endpoint.getEndpointId());
    FavoriteEntity favorite = new FavoriteEntity();
    ReflectionTestUtils.setField(favorite, "id", favoriteId);
    ReflectionTestUtils.setField(favorite, "user", user);
    ReflectionTestUtils.setField(favorite, "endpoint", endpoint);

    favoriteRepository.save(favorite);

    // Verificar
    Optional<FavoriteEntity> foundFavorite = favoriteRepository.findById(favoriteId);
    assertTrue(foundFavorite.isPresent(), "Favorite should be found");

    FavoriteEntity found = foundFavorite.get();
    FavoriteEntity.FavoriteId foundId =
        (FavoriteEntity.FavoriteId) ReflectionTestUtils.getField(found, "id");

    assertEquals(favoriteId, foundId);
  }

  @Test
  @Order(2)
  public void givenFavoriteRepository_whenUpdateFavorite_thenOK() {
    // Crear datos: categoría
    CategoryEntity category =
        categoryRepository
            .findByName("TEST")
            .orElseGet(() -> categoryRepository.save(new CategoryEntity("TEST", "#FFFFFF")));

    // Crear datos: usuario
    UserEntity user =
        userRepository
            .findByEmail("johndoe@autozone.com")
            .orElseGet(
                () ->
                    userRepository.save(
                        new UserEntity("johndoe@autozone.com", true, UserRoleEnum.USER)));

    // Crear datos: endpoint original
    EndpointsEntity originalEndpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "OriginalEndpoint",
                "Original favorite endpoint",
                EndpointMethodEnum.GET,
                "https://original.favorite"));

    // Crear y guardar el Favorite original
    FavoriteEntity.FavoriteId originalFavoriteId =
        new FavoriteEntity.FavoriteId(user.getUserId(), originalEndpoint.getEndpointId());
    FavoriteEntity favorite = new FavoriteEntity();
    ReflectionTestUtils.setField(favorite, "id", originalFavoriteId);
    ReflectionTestUtils.setField(favorite, "user", user);
    ReflectionTestUtils.setField(favorite, "endpoint", originalEndpoint);
    favoriteRepository.save(favorite);

    // Crear datos: nuevo endpoint
    EndpointsEntity newEndpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "UpdatedFavoriteEndpoint",
                "Updated favorite endpoint",
                EndpointMethodEnum.POST,
                "https://updated.favorite"));

    // Crear el nuevo Favorite (nuevo ID)
    FavoriteEntity.FavoriteId newFavoriteId =
        new FavoriteEntity.FavoriteId(user.getUserId(), newEndpoint.getEndpointId());

    // Eliminar el original y guardar el nuevo
    favoriteRepository.deleteById(originalFavoriteId);

    FavoriteEntity updatedFavorite = new FavoriteEntity();
    ReflectionTestUtils.setField(updatedFavorite, "id", newFavoriteId);
    ReflectionTestUtils.setField(updatedFavorite, "user", user);
    ReflectionTestUtils.setField(updatedFavorite, "endpoint", newEndpoint);
    favoriteRepository.save(updatedFavorite);

    // Verificar
    Optional<FavoriteEntity> foundFavoriteOptional = favoriteRepository.findById(newFavoriteId);
    assertTrue(foundFavoriteOptional.isPresent(), "Updated Favorite should be found");

    FavoriteEntity foundFavorite = foundFavoriteOptional.get();
    FavoriteEntity.FavoriteId foundId =
        (FavoriteEntity.FavoriteId) ReflectionTestUtils.getField(foundFavorite, "id");

    assertEquals(newFavoriteId, foundId);
  }

  @Test
  @Order(3)
  public void givenFavoriteRepository_whenDeleteFavorite_thenOK() {
    // Crear datos necesarios
    CategoryEntity category =
        categoryRepository
            .findByName("TEST")
            .orElseGet(() -> categoryRepository.save(new CategoryEntity("TEST", "#FFFFFF")));

    UserEntity user =
        userRepository
            .findByEmail("johndoe@autozone.com")
            .orElseGet(
                () ->
                    userRepository.save(
                        new UserEntity("johndoe@autozone.com", true, UserRoleEnum.USER)));

    EndpointsEntity endpoint =
        endpointsRepository.save(
            new EndpointsEntity(
                category,
                user,
                true,
                "EndpointToDelete",
                "Endpoint for deletion",
                EndpointMethodEnum.GET,
                "https://delete.favorite"));

    FavoriteEntity.FavoriteId favoriteId =
        new FavoriteEntity.FavoriteId(user.getUserId(), endpoint.getEndpointId());

    FavoriteEntity favorite = new FavoriteEntity();
    ReflectionTestUtils.setField(favorite, "id", favoriteId);
    ReflectionTestUtils.setField(favorite, "user", user);
    ReflectionTestUtils.setField(favorite, "endpoint", endpoint);

    favoriteRepository.save(favorite);

    // Asegurarse de que el Favorite fue guardado
    Optional<FavoriteEntity> savedFavoriteOptional = favoriteRepository.findById(favoriteId);
    assertTrue(savedFavoriteOptional.isPresent(), "Favorite should be present before deletion");

    // Eliminar el Favorite
    favoriteRepository.deleteById(favoriteId);

    // Verificar que ya no exista
    Optional<FavoriteEntity> deletedFavoriteOptional = favoriteRepository.findById(favoriteId);
    assertFalse(deletedFavoriteOptional.isPresent(), "Favorite should be deleted");
  }
}
