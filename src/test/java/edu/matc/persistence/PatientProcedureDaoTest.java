package edu.matc.persistence;

import edu.matc.entity.Patient;
import edu.matc.entity.PatientProcedure;
import edu.matc.test.util.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This program is testing the PatientProcedureDao
 */
class PatientProcedureDaoTest {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * The patient dao.
     */
    GenericDao genericDao;
    GenericDao genericDao2;

    /**
     * Sets up. Run sql to recreate the database before each test
     */
    @BeforeEach
    void setUp() {
        genericDao = new GenericDao(PatientProcedure.class);

        Database database = Database.getInstance();
        database.runSQL("cleanpatientdb.sql");
    }

    /**
     * Gets the patient's procedure by id.
     */
    @Test
    void getPatientProcedureByIdIsSuccessful() {
        PatientProcedure procedure = (PatientProcedure)genericDao.getById(1);
        assertEquals(97001, procedure.getProcedureCode());
    }

    /**
     * Gets the all procedures for a patient (by FK)
     */
    @Test
    void getAllProceduresForPatient() {
        List<PatientProcedure> procedure = (List<PatientProcedure>)genericDao.getByColumnInt("patient", 1);
        assertEquals(3, procedure.size());
    }


    /**
     * Gets the all procedure on the database
     */
    @Test
    void getAllProcedures() {
        List<PatientProcedure> procedure = (List<PatientProcedure>)genericDao.getAllByTable();
        assertEquals(7, procedure.size());
    }

    /**
     * patient procedure code is updates successfully.
     */
    @Test
    void UpdatedProcedureSuccessfully() {

        int newCode = 888888;

        PatientProcedure procedureToUpdate = (PatientProcedure)genericDao.getById(6);
        procedureToUpdate.setProcedureCode(newCode);
        genericDao.saveOrUpdate(procedureToUpdate);

        PatientProcedure changedProcedure = (PatientProcedure)genericDao.getById(6);
        logger.info("Code should be updated {}", changedProcedure.getProcedureCode());
        assertEquals(procedureToUpdate, changedProcedure);
    }

    /**
     * Verify successful insert of a procedure for an existing patient
     */
    @Test
    void insertProcedureForPatientIsSuccessful() {

        //GenericDao patientUpdate = new GenericDao(Patient.class);
        genericDao2 = new GenericDao(Patient.class);

        //get a patient and add a procedure
        Patient patient = (Patient)genericDao2.getById(3);
        PatientProcedure newProc = new PatientProcedure(777777, LocalDateTime.parse("2018-02-17T10:25:10"), patient);
        patient.addProcedures(newProc);

        //insert the procedure for the patient
        int id = genericDao.insert(newProc);
        assertNotEquals(0, id);

        //test that the correct procedure got added to the intended patient
        PatientProcedure testProc = (PatientProcedure)genericDao.getById(id);
        assertEquals(newProc, testProc);

        //test that nothing changed for the patient
        Patient patientWithProc = (Patient)genericDao2.getById(3);
        assertEquals(patient, patientWithProc);

        //test that the correct procedure got added to the correct patient
        assertNotNull(testProc.getPatient());
        assertEquals(3, testProc.getPatient().getId());

        //http://docs.jboss.org/hibernate/orm/5.2/orderguide/html_single/Hibernate_Order_Guide.html#mapping-model-pojo-equalshashcode
    }

    /**
     * Delete procedure is successful.
     */
    @Test
    void deleteProcedureIsSuccessful() {
        //ppDao.delete(ppDao.getPatientProceduresById(3));
        genericDao.delete(genericDao.getById(3));
        assertNull(genericDao.getById(3));
    }
}