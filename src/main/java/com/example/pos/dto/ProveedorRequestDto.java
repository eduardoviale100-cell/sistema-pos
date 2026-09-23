package com.example.pos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequestDto {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String nombre;

    @Size(max = 20, message = "El RUC o DNI no puede superar los 20 caracteres")
    private String rucDni;

    // Alias compatible con frontends que envían rfc
    private String rfc;

    // Contacto opcional
    private String contacto;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    private String direccion;

    public String getRucDni() {
        if (rucDni != null && !rucDni.isBlank()) {
            return rucDni.trim();
        }
        if (rfc != null && !rfc.isBlank()) {
            return rfc.trim();
        }
        return null;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
        if (this.rucDni == null || this.rucDni.isBlank()) {
            this.rucDni = rfc;
        }
    }

    public String getEmail() {
        return (email != null && !email.isBlank()) ? email.trim() : null;
    }
}
