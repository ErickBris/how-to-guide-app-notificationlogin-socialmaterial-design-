
<?php 
    function send_notification ($tokens, $message,$key)
    {
        $url = 'https://fcm.googleapis.com/fcm/send';
        $fields = array(
             'registration_ids' => $tokens,
             'data' => $message

            );
        $headers = array(
            'Authorization:key = '.$key,
            'Content-Type: application/json'
            );
       $ch = curl_init();
       curl_setopt($ch, CURLOPT_URL, $url);
       curl_setopt($ch, CURLOPT_POST, true);
       curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
       curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
       curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);  
       curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
       curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
       $result = curl_exec($ch);           
       if ($result === FALSE) {
           die('Curl failed: ' . curl_error($ch));
       }
       curl_close($ch);
       return $result;
    }
          $message = array(
                      "id"=>$guide->getId(),
                      "title"=>$guide->getTitle(),
                      "time"=>$view['time']->diff($guide->getCreated()),
                      "category"=>$guide->getCategory()?$guide->getCategory()->getTitle(): null,
                      "image"=> $this['imagine']->filter($view['assets']->getUrl($guide->getMedia()->getLink()), 'api_img'),
                      );
        $key=$this->container->getParameter('fire_base_key');
        $message_status = send_notification($tokens, $message,$key);
        echo $message_status;
        header("Location: ".$view['router']->path('app_guides_view',array("id"=>$guide->getId())));
        die();

 ?>