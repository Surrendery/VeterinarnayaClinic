
import lombok.Getter;
import lombok.Setter;



import java.text.SimpleDateFormat;
import java.util.*;

public class App {
    public static void main(String[] args) {
        AppStart.start();
    }
    public static class AppStart {
        private static final PatientServiceRlztn patientService = new PatientServiceRlztn();
        private static final DoctorServiceRlztn doctorService = new DoctorServiceRlztn();
        private static final AppointmentServiceRlztn appointmentService = new AppointmentServiceRlztn();

        public static void start() {
            Scanner scanner = new Scanner(System.in);
            if (Authentication.authentication()) while (true) {
                System.out.print("""
                    0 - выйти
                    1 - создать пациента
                    2 - удалить пациента
                    3 - создать доктора
                    4 - создать приём
                    5 - показать всех пациентов
                    6 - изменить фио пациента
                    7 - показать все приёмы пациента
                    8 - изменить статус приёма
                    """);
                switch (scanner.nextInt()) {
                    case 0 -> System.exit(0);
                    case 1 -> PatientServiceRlztn.createPatient();
                    case 2 -> PatientServiceRlztn.deletePatient();
                    case 3 -> DoctorServiceRlztn.createDoctor();
                    case 4 -> appointmentService.createAppointment();
                    case 5 -> PatientServiceRlztn.showAllPatients();
                    case 6 -> PatientServiceRlztn.editPatientFcs();
                    case 7 -> AppointmentServiceRlztn.showAllPatientAppointments();
                    case 8 -> AppointmentServiceRlztn.changeAppointmentStatus();
                }
            }
            else {
                System.out.println("Неправильный логин или пароль!");
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

        public static boolean authentication() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите логин: ");
            String login = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();

            return login.equals(LOGIN) & password.equals(PASSWORD);
        }
    }
    @Getter
    @Setter
    public static class Patient {

        private UUID id = UUID.randomUUID();

        private String fcs;

        private Calendar registrationDate;

        public String getRegistrationDate(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(registrationDate.getTime());
        }

        public Patient(String fcs, Calendar registrationDate) {

            this.fcs = fcs;
            this.registrationDate = registrationDate;
        }
    }
    @Getter
    @Setter
    public static class Doctor {
        private UUID id = UUID.randomUUID();

        private String fcs;

        public Doctor(String fcs) {
            this.fcs = fcs;
        }
    }
    @Getter
    @Setter
    public static class Appointment {

        private UUID id = UUID.randomUUID();

        private String patientFcs;
        private Calendar registrationDate;

        private AppointmentStatus status;

        public String getRegistrationDate(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(registrationDate.getTime());
        }
        public Appointment(String patientFcs, Calendar registrationDate,AppointmentStatus status){

            this.patientFcs = patientFcs;
            this.registrationDate = registrationDate;
            this.status = status;
        }
    }
    public static class PatientServiceRlztn {



        public static void createPatient() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите фио: ");
            String fcs = scanner.nextLine();
            System.out.print("Введите год регистрации: ");
            int year = scanner.nextInt();
            System.out.print("Введите месяц регистрации: ");
            int month = scanner.nextInt();
            System.out.print("Введите день регистрации: ");
            int day = scanner.nextInt();
            Calendar registrationDate = new GregorianCalendar(year, month, day);

            Storage.patients.add(new Patient(fcs, registrationDate));
            System.out.println("Пациент был добавлен!");
        }

        public static void showAllPatients() {
            if (Storage.patients.isEmpty()) {
                System.out.println("Пациентов нет");
                return;
            }

            Storage.patients.forEach(p -> {
                System.out.printf("ID: %s\nфио: %s\nДата регистрации: %s", p.getId(), p.getFcs(), p.getRegistrationDate());
                System.out.print("\n");
            });
        }

        public static void deletePatient() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите ID пациента: ");
            UUID patientId = UUID.fromString(scanner.nextLine());
            Storage.patients.removeIf(p -> p.getId().equals(patientId));

            System.out.println("Пациент был удалён!");
        }

        public static void editPatientFcs() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Введите ID пользователя: ");
            UUID patientId = UUID.fromString(scanner.nextLine());
            Patient patient = Storage.patients.stream().filter(p -> p.getId().equals(patientId)).findFirst().get();


            System.out.print("Введите фио: ");
            String fcs = scanner.nextLine();

            patient.setFcs(fcs);
        }
    }
    public static class DoctorServiceRlztn {


        public static void createDoctor() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите фио: ");
            String fcs = scanner.nextLine();

            Storage.doctors.add(new Doctor(fcs));
            System.out.println("Доктор " + fcs + " был добавлен!");
        }
    }
    public static class AppointmentServiceRlztn {


        public void createAppointment() {

            Scanner scanner = new Scanner(System.in);

            System.out.print("Введите фио пациента: ");
            String patientFcs = scanner.nextLine();
            System.out.print("Введите год регистрации: ");
            int year = scanner.nextInt();
            System.out.print("Введите месяц регистрации: ");
            int month = scanner.nextInt();
            System.out.print("Введите день регистрации: ");
            int day = scanner.nextInt();
            Calendar registrationDate = new GregorianCalendar(year, month, day);

            Appointment appointment = new Appointment(patientFcs, registrationDate, AppointmentStatus.NEW);

            Storage.appointments.add(appointment);

        }

        public static void showAllPatientAppointments() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Введите фио пациента: ");
            String patientFcs = scanner.nextLine();

            if (Storage.appointments.stream().noneMatch(a -> a.getPatientFcs().equals(patientFcs))) {
                System.out.println("У этого пациента нет приёмов");
                return;
            }

            System.out.println("Список приёмов для " + patientFcs);
            Storage.appointments.forEach(a -> {
                if (a.getPatientFcs().equals(patientFcs))
                    System.out.println(a.getRegistrationDate() + " " + a.getStatus().toString());
            });

        }

        public static void changeAppointmentStatus() {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Введите ID приёма: ");
            UUID appointmentId = UUID.fromString(scanner.nextLine());

            System.out.print("""
                Выберите новый статус:
                0 - Новый
                1 - В процессе
                2 - Отменён
                3 - Ожидается оплата
                4 - Выполнен
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
    public enum AppointmentStatus {
        NEW("Новый"),
        IN_PROCESS("В процессе"),
        CANCELLED("Отменён"),
        AWAITING_PAYMENT("Ожидается оплата"),
        COMPLETED("Выполнен");

        private final String text;

        AppointmentStatus(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}

