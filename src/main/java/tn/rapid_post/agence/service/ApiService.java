package tn.rapid_post.agence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.repo.b3Repo;

import java.util.List;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    @Autowired
    private b3Repo b3Rep;

    // Constructeur pour injecter RestTemplate
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean getApiData(B3 b3,String ipRecu,String date) {
        String apiUrl = "http://"+ipRecu+":8080/send_sms";
        String messageAr = "";
        // Message en arabe fixe
        if (b3!=null) {

                if (b3.getDestination().equals("Agence")) {
                    messageAr = " البريد التونسي يرحب بكم و يعلمكم أن البطاقة عدد 3 موجودة بوكالة البريد السريع باب بحر صفاقس "+b3.getIdB3();
                }if (!b3.getDestination().equals("Agence")) {
                    messageAr = " البريد التونسي يرحب بكم و يعلمكم أنه بامكانكم إستلام البطاقة عدد 3 يوم : "+date+"  بمكتب بريد" + b3.getDestination();
                }
                // Construction du message bilingue
                String fullMessage = messageAr + " "+b3.getNumB3()+" " ;

              try {
                    String fullUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
                            .queryParam("numero", b3.getNumTel())
                            .queryParam("message", fullMessage)
                            .toUriString();

                     restTemplate.getForObject(fullUrl, String.class);
                     b3.setNotified(true);
                     b3Rep.save(b3);
                  System.out.println(fullMessage);
                     return true;

                } catch (Exception e) {
                  b3.setNotified(false);
                    b3Rep.save(b3);
                    return false;
                }

            }


        return false;
    }}
