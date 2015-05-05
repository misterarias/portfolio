<?php

namespace AF\HomeBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class DefaultController extends Controller
{
    public function indexAction()
    {
        return $this->render('AFHomeBundle:Default:index.html.twig', array('name' => "Mundo"));
    }
}
