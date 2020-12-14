package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class TeamDTO {
    private Long id;
    private String name;
    private int status;
    private List<StudentDTO> members;
    private List<VmInstanceDTO> vmInstances;
    private StudentDTO owner;
    private Map<String,String> members_status;
    private String confirmation_token;
    private Timestamp expiry_date;
}
