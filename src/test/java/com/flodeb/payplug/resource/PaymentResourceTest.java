package com.flodeb.payplug.resource;

import com.flodeb.payplug.client.APIRoutes;
import com.flodeb.payplug.client.HttpClient;
import com.flodeb.payplug.core.PayplugConfiguration;
import com.flodeb.payplug.exception.PayplugException;
import com.flodeb.payplug.exception.UndefinedAttributeException;
import com.flodeb.payplug.model.Payment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(APIRoutes.class)
public class PaymentResourceTest {

    @InjectMocks
    private PaymentResource paymentResource;

    @Mock
    private HttpClient httpClient;

    @Test(expected = UndefinedAttributeException.class)
    public void shouldNotGetResourceForNoPaymentId() throws PayplugException {
        // Given
        PayplugConfiguration configuration = new PayplugConfiguration("8gf1fg7e1erg");

        Payment payment = new Payment();
        paymentResource.setPayment(payment);

        // When
        paymentResource.getConsistentResource(configuration);

        // Then
    }

    @Test
    public void shouldGetResource() throws PayplugException {
        // Given
        PowerMockito.mockStatic(APIRoutes.class);

        PayplugConfiguration configuration = new PayplugConfiguration("8gf1fg7e1erg");

        Payment originalPayment = new Payment();
        originalPayment.setId("OLD_ID");
        paymentResource.setPayment(originalPayment);

        Payment realPayment = new Payment();
        realPayment.setId("NEW_ID");

        when(APIRoutes.getRoute(APIRoutes.PAYMENT_RESOURCE, "OLD_ID")).thenReturn("http://payments.com/OLD_ID");
        PowerMockito.when(httpClient.get(configuration, "http://payments.com/OLD_ID", Payment.class)).thenReturn(realPayment);

        // When
        Payment retrievedPayment = paymentResource.getConsistentResource(configuration);

        // Then
        Assert.assertEquals(realPayment, retrievedPayment);
        Assert.assertEquals("NEW_ID", retrievedPayment.getId());
    }
}