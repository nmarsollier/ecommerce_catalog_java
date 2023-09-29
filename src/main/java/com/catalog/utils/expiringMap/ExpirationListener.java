package com.catalog.utils.expiringMap;

/**
 * A listener for expired object events.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 589932 $, $Date: 2007-10-30 02:50:39 +0100 (Tue, 30 Oct 2007) $
 */
interface ExpirationListener<E> {
    void expired(E expiredObject);
}
