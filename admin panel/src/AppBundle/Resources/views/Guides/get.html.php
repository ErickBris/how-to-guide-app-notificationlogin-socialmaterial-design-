<?php 
	$a["id"]=$guide->getId();
	$a["title"]=$guide->getTitle();
	$a["category"]=$guide->getCategory()?$guide->getCategory()->getTitle(): null ;
	$a["content"]=$guide->getContent();
	$a["comment"]=$guide->getComment();
	$a["image"]= $this['imagine']->filter($view['assets']->getUrl($guide->getMedia()->getLink()), 'api_img');
	$a["created"]=$view['time']->diff($guide->getCreated());
	$list=array();
	foreach ($guide->getSteps() as $key => $step) {
		$s["id"]=$step->getId();
		$s["title"]=$step->getTitle();
		$s["position"]=$step->getPosition();
		$s["content"]=$step->getContent();
		if ($step->getMedia()!=null) {
			$s["image"]= $this['imagine']->filter($view['assets']->getUrl($step->getMedia()->getLink()), 'api_img');
		}else{
			$s["image"]= null;			
		}

		$list[]=$s;
	}
	$a["steps"]=$list;
	$comments=array();
	foreach ($guide->getComments() as $key => $comment) {
		$c["id"]=$comment->getId();
		$c["content"]=$comment->getContent();
		$c["enabled"]=$comment->getEnabled();
		$c["author"]=$comment->getUser()->getName();

        if ($comment->getUser()->getMedia()==null) {
            $c["image"]=$this['imagine']->filter("img/default_male.png", 'profile_picture');        
        }else{
            $c["image"]=$this['imagine']->filter($comment->getUser()->getMedia()->getLink(), 'profile_picture');        
        }
		$c["created"]=$view['time']->diff($comment->getCreated());
		$comments[]=$c;
	}
	$a["comments"]=$comments;
	echo json_encode($a, JSON_UNESCAPED_UNICODE);
?>