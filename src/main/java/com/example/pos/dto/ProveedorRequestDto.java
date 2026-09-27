package com.example.pos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequestDto {

    @NotBlank(message = "La razón social no puede estar vacía")
    @Size(min = 3, max = 150, message = "La razón social debe tener al menos 3 caracteres")
    private String nombre;

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "^(10|20)\\d{9}$", message = "El RUC debe tener 11 dígitos y empezar con 10 o 20")
    private String rucDni;

    // Alias compatible con frontends que envían rfc
    private String rfc;

    // Contacto opcional
    private String contacto;

    @NotBlank(message = "El celular es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "El celular debe tener 9 dígitos y empezar con 9")
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ingresar un correo electrónico válido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 200, message = "La dirección debe tener al menos 5 caracteres")
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
