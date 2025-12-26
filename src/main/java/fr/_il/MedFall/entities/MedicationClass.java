package fr._il.MedFall.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

// Entité JPA pour la classe de médicament
@Entity
@Table(name = "medication_class")
@Data
@EqualsAndHashCode(exclude = {"molecules", "condition"}) 
@ToString(exclude = {"molecules", "condition"}) 
public class MedicationClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

// MANY-TO-MANY – côté propriétaire
    @ManyToMany
    @JoinTable(
            name = "molecule_medication_class",
            joinColumns = @JoinColumn(name = "medication_class_id "),
            inverseJoinColumns = @JoinColumn(name = "molecule_id")
    )
    private Set<Molecule> molecules = new HashSet<>();

    // Relation One-to-One avec ClassCountCondition (MedicationClass est le côté inverse)
    @OneToOne(mappedBy = "medicationClass", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ClassCountCondition condition;

    public void addMolecule(Molecule molecule) {
        if (this.molecules == null) {
            this.molecules = new HashSet<>();
        }
        this.molecules.add(molecule);

        if (molecule.getMedicationClasses() == null) {
            molecule.setMedicationClasses(new HashSet<>());
        }
        molecule.getMedicationClasses().add(this);
    }

    public void removeMolecules(Molecule molecule) {
        if (this.molecules != null) {
            this.molecules.remove(molecule);
        }
        if (molecule.getMedicationClasses() != null) {
            molecule.getMedicationClasses().remove(this);
        }
    }
}