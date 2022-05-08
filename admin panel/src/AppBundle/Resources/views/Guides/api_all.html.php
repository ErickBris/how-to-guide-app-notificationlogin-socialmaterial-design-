<?php 
$list=array();
foreach ($guides as $key => $guide) {
	$a["id"]=$guide->getId();
	$a["title"]=$guide->getTitle();
	$a["category"]=$guide->getCategory()?$guide->getCategory()->getTitle(): null ;
	
	$a["content"]=$guide->getContent();
	$a["comment"]=$guide->getComment();
	$a["image"]= $this['imagine']->filter($view['assets']->getUrl($guide->getMedia()->getLink()), 'api_img');
	$a["created"]=$view['time']->diff($guide->getCreated());
	$list[]=$a;
}
echo json_encode($list, JSON_UNESCAPED_UNICODE);
?>