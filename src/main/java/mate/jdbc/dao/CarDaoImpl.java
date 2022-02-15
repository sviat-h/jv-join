package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        return null;
    }

    @Override
    public Car get(Long id) {
        String getQuery = "SELECT c.id AS car_id, model, m.id "
                + "AS manufacturer_id, m.name, m.country"
                + " FROM taxi.cars c JOIN taxi.manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.id = ?;";
        Car carFromDB = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                carFromDB = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        return carFromDB;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = Manufacturer.builder()
                .id(resultSet.getObject("manufacturer_id", Long.class))
                .name(resultSet.getString("name"))
                .country(resultSet.getString("country"))
                .build();
        List<Driver> drivers = getDriversForCar(resultSet.getObject("car_id", Long.class));
        return Car.builder()
                .id(resultSet.getObject("car_id", Long.class))
                .model(resultSet.getString("model"))
                .manufacturer(manufacturer)
                .drivers(drivers)
                .build();
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarQuery = "SELECT id, name, license_number "
                + "FROM taxi.drivers d JOIN taxi.cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ?";
        List<Driver> driversForCar = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(getAllDriversForCarQuery)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                driversForCar.add(Driver.builder()
                        .id(resultSet.getObject("id", Long.class))
                        .name(resultSet.getString("name"))
                        .licenseNumber(resultSet.getString("license_number"))
                        .build());
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for car by car_id " + carId, e);
        }
        return driversForCar;
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }
}