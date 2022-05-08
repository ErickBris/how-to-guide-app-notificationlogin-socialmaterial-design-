<?php

namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class ElementsController extends Controller
{

    public function menuAction()
    {
    	return $this->render("AppBundle:Elements:menu.html.twig");
    }
}
