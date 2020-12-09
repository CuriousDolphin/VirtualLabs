package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Course;
import it.polito.ai.virtualLabs.entities.VmModel;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public interface VmModelRepository extends JpaRepository<VmModel,Long> {
    VmModel getByCourse(Course course);

}
