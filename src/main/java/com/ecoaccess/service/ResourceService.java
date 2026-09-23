package com.ecoaccess.service;

import java.util.List;
import java.util.Optional;
import com.ecoaccess.dao.ResourceDao;
import com.ecoaccess.exception.AppExceptions.ValidationException;
import com.ecoaccess.model.Resource;
import com.ecoaccess.model.Enums.ServiceType;
import com.ecoaccess.util.Ids;
import com.ecoaccess.util.Validation;

/** Application operations for station-resource inventory. */
public class ResourceService {
    private final ResourceDao resourceDao;

    public ResourceService() { this(new ResourceDao()); }
    public ResourceService(ResourceDao resourceDao) { this.resourceDao = resourceDao; }

    public Optional<Resource> find(String station, ServiceType serviceType) {
        Validation.required(station, "Station is required.");
        if (serviceType == null) throw new ValidationException("Service type is required.");
        return resourceDao.find(station, serviceType);
    }

    public List<Resource> findAll(String stationSearch) {
        return resourceDao.findAll(stationSearch == null ? "" : stationSearch.trim());
    }

    public void addOrMerge(String station, ServiceType serviceType, int quantity) {
        Validation.required(station, "Station is required.");
        if (serviceType == null) throw new ValidationException("Service type is required.");
        if (quantity < 0) throw new ValidationException("Quantity cannot be negative.");
        resourceDao.addOrMerge(Ids.next("RS"), station.trim(), serviceType, quantity);
    }

    public void updateQuantity(String resourceId, int quantity) {
        Validation.required(resourceId, "Resource ID is required.");
        if (quantity < 0) throw new ValidationException("Quantity cannot be negative.");
        resourceDao.updateQuantity(resourceId, quantity);
    }

    public void delete(String resourceId) {
        Validation.required(resourceId, "Resource ID is required.");
        resourceDao.delete(resourceId);
    }
}
