import axios from "axios";
import {ElMessage} from "element-plus";

// default error handler
const defaultError = () => ElMessage.error('Some error occurs, please contact the admin.')

// default failure handler
const defaultFailure = (message) => {
    console.log(message)
    ElMessage.warning(message)
}

/**
 * post request
 *
 * @param url the url
 * @param data the data
 * @param success the success handler
 * @param failure the failure handler
 * @param error the error handler
 */
function post(url, data, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        withCredentials: true // enable session, because we don't use jwt we need this
    }).then(({data}) => {
        if(data.success) {
            success(data.message, data.status)
        } else {
            failure(data.message, data.status)
        }
    })
}

/**
 * get request
 *
 * @param url the url
 * @param success the success handler
 * @param failure the failure handler
 * @param error the error handler
 */
function get(url, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, {
        withCredentials: true // enable session, because we don't use jwt we need this
    }).then(({data}) => {
        if(data.success) {
            success(data.message, data.status)
        } else {
            failure(data.message, data.status)
        }
    })
}

export { get, post }