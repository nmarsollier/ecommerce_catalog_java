package com.catalog.utils.errors;

import com.catalog.utils.gson.JsonSerializable;

public interface JsonError extends JsonSerializable {
    int statusCode();
}
