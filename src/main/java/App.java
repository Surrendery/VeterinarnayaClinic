
import lombok.Getter;
import lombok.Setter;



import java.text.SimpleDateFormat;
import java.util.*;

public class App {
    public static void main(String[] args) {
        AppStart.start();
    }
    public static class AppStart {
        private static final PatientServiceImpl patientService = new PatientServiceImpl();
        private static final DoctorServiceImpl doctorService = new DoctorServiceImpl();
        private static final AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();

        public static void start() {
            Scanner scanner = new Scanner(System.in);
            if (Authentication.auth()) while (true) {
                System.out.print("""
                    0 - Escape
                    1 - Create patient
                    2 - Create doctor
                    3 - Create appointment
                    4 - Delete patient
                    5 - Show all patients
                    6 - Edit patients fio
                    7 - Show all patient's appointments
                    8 - Change appointment status
                    """);
                switch (scanner.nextInt()) {
                    case 0 -> System.exit(0);
                    case 1 -> PatientServiceImpl.createPatient();
                    case 2 -> DoctorServiceImpl.createDoctor();
                    case 3 -> appointmentService.createAppointment();
                    case 4 -> PatientServiceImpl.deleteUser();
                    case 5 -> PatientServiceImpl.showAllPatients();
                    case 6 -> PatientServiceImpl.editPatientFio();
                    case 7 -> AppointmentServiceImpl.showAllPatientAppointments();
                    case 8 -> AppointmentServiceImpl.changeAppointmentStatus();
                }
            }
            else {
                System.out.println("Incorrect!");
            }
        }
    }
    public static class Storage {
        public static List<Patient> patients = new ArrayList<>();
        public static List<Doctor> doctors = new ArrayList<>();
        public static List<Appointment> appointments = new ArrayList<>();
    }
    public static class Authentication {
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";

        public static boolean auth() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter login: ");
            String login = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            return login.equals(LOGIN) & password.equals(PASSWORD);
        }
    }
    @Getter
    @Setter
    public static class Appointment {

        private UUID id = UUID.randomUUID();

        private String patientFio;
        private Calendar registrationDate;

        private AppointmentStatus status;

        public String getRegistrationDate(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(registrationDate.getTime());
        }
        public Appointment(String patientFio, Calendar registrationDate,AppointmentStatus status){

            this.patientFio = patientFio;
            this.registrationDate = registrationDate;
            this.status = status;
        }
    }
    public static class AppointmentServiceImpl {


        public void createAppointment() {

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter patient fio: ");
            String patientFio = scanner.nextLine();
            System.out.print("Enter reg year: ");
            int year = scanner.nextInt();
            System.out.print("Enter reg month: ");
            int month = scanner.nextInt();
            System.out.print("Enter reg day: ");
            int day = scanner.nextInt();
            Calendar registrationDate = new GregorianCalendar(year, month, day);

            Appointment appointment = new Appointment(patientFio, registrationDate, AppointmentStatus.NEW);

            Storage.appointments.add(appointment);

        }

        public static void showAllPatientAppointments() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter patient fio: ");
            String patientFio = scanner.nextLine();

            if (Storage.appointments.stream().noneMatch(a -> a.getPatientFio().equals(patientFio))) {
                System.out.println("This patient has no appointments");
                return;
            }

            System.out.println("List of appointments for " + patientFio);
            Storage.appointments.forEach(a -> {
                if (a.getPatientFio().equals(patientFio))
                    System.out.println(a.getRegistrationDate() + " " + a.getStatus().toString());
            });

        }

        public static void changeAppointmentStatus() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter appointment id: ");
            UUID appointmentId = UUID.fromString(scanner.nextLine());

            System.out.print("""
                Enter new status:
                0 - NEW
                1 - IN PROCESS
                2 - CANCELLED
                3 - AWAITING PAYMENT
                4 - COMPLETED
                """);

            int statusCode = scanner.nextInt();
            AppointmentStatus newStatus = null;

            switch (statusCode) {
                case 0 -> newStatus = AppointmentStatus.NEW;
                case 1 -> newStatus = AppointmentStatus.IN_PROCESS;
                case 2 -> newStatus = AppointmentStatus.CANCELLED;
                case 3 -> newStatus = AppointmentStatus.AWAITING_PAYMENT;
                case 4 -> newStatus = AppointmentStatus.COMPLETED;
            }

            Storage.appointments.stream().filter(a -> a.getId().equals(appointmentId)).findFirst().get().setStatus(newStatus);

        }
    }
    public static class DoctorServiceImpl {


        public static void createDoctor() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter fio: ");
            String fio = scanner.nextLine();

            Storage.doctors.add(new Doctor(fio));
            System.out.println("Dr " + fio + " was added!");
        }
    }
    public static class PatientServiceImpl {



        public static void createPatient() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter fio: ");
            String fio = scanner.nextLine();
            System.out.print("Enter reg year: ");
            int year = scanner.nextInt();
            System.out.print("Enter reg month: ");
            int month = scanner.nextInt();
            System.out.print("Enter reg day: ");
            int day = scanner.nextInt();
            Calendar registrationDate = new GregorianCalendar(year, month, day);

            Storage.patients.add(new Patient(fio, registrationDate));
            System.out.println("Patient was added!");
        }

        public static void showAllPatients() {
            if (Storage.patients.isEmpty()) {
                System.out.println("No patients");
                return;
            }

            Storage.patients.forEach(p -> {
                System.out.printf("ID: %s\nFIO: %s\nRegistration date: %s", p.getId(), p.getFio(), p.getRegistrationDate());
                System.out.println("\n");
            });
        }

        public static void deleteUser() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter user id: ");
            UUID patientId = UUID.fromString(scanner.nextLine());
            Storage.patients.removeIf(p -> p.getId().equals(patientId));

            System.out.println("User was delete!");
        }

        public static void editPatientFio() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter user id: ");
            UUID patientId = UUID.fromString(scanner.nextLine());
            Patient patient = Storage.patients.stream().filter(p -> p.getId().equals(patientId)).findFirst().get();


            System.out.print("Enter fio: ");
            String fio = scanner.nextLine();

            patient.setFio(fio);
        }
    }
    public enum AppointmentStatus {
        NEW("NEW"),
        IN_PROCESS("IN-PROCESS"),
        CANCELLED("CANCELLED"),
        AWAITING_PAYMENT("AWAITING-PAYMENT"),
        COMPLETED("COMPLETED");

        private final String text;

        AppointmentStatus(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    @Getter
    @Setter
    public static class Doctor {
        private UUID id = UUID.randomUUID();

        private String fio;

        public Doctor(String fio) {
            this.fio = fio;
        }
    }
    @Getter
    @Setter
    public static class Patient {

        private UUID id = UUID.randomUUID();

        private String fio;

        private Calendar registrationDate;

        public String getRegistrationDate(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(registrationDate.getTime());
        }

        public Patient(String fio, Calendar registrationDate) {

            this.fio = fio;
            this.registrationDate = registrationDate;
        }
    }

}

