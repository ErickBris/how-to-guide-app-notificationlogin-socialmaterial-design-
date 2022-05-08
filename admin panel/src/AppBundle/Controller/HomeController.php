<?php

namespace AppBundle\Controller;
use AppBundle\Entity\Comment;
use AppBundle\Entity\Device;
use AppBundle\Entity\Category;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Serializer\Serializer;
use Symfony\Component\Serializer\Encoder\XmlEncoder;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\HttpFoundation\Response;
class HomeController extends Controller
{
    public function indexAction()
    {   

    	$em=$this->getDoctrine()->getManager();
        $categories= $em->getRepository("AppBundle:Category")->findAll();
        $categories_count= sizeof($categories);


        $users= $em->getRepository("UserBundle:User")->findAll();
        $users_count= sizeof($users)-1;

        $comments= $em->getRepository("AppBundle:Comment")->findAll();
        $comments_count= sizeof($comments);

        $messages= $em->getRepository("AppBundle:Support")->findAll();
        $messages_count= sizeof($messages);
        
        $guides= $em->getRepository("AppBundle:Guide")->findAll();
        $guides_count= sizeof($guides);

        $steps= $em->getRepository("AppBundle:Step")->findAll();
        $steps_count= sizeof($steps);



        $articles_count=0;
        $videos_count=0;



        return $this->render('AppBundle:Home:index.html.twig',array(
            "categories_count"=>$categories_count,
            "users_count"=>$users_count,
            "comments_count"=>$comments_count,
            "guides_count"=>$guides_count,
            "steps_count"=>$steps_count,
            "messages_count"=>$messages_count
        ));
    }
    public function api_deviceAction($tkn,$token){
        if ($token!=$this->container->getParameter('token_app')) {
            throw new NotFoundHttpException("Page not found");  
        }
        $code="200";
        $message="";
        $errors=array();
        $em = $this->getDoctrine()->getManager();
        $d=$em->getRepository('AppBundle:Device')->findOneBy(array("token"=>$tkn));
        if ($d==null) {
            $device = new Device();
            $device->setToken($tkn);
            $em->persist($device);
            $em->flush();
            $message="Deivce added";
        }else{
            $message="Deivce Exist";
        }

        $error=array(
            "code"=>$code,
            "message"=>$message,
            "values"=>$errors
        );
        $encoders = array(new XmlEncoder(), new JsonEncoder());
        $normalizers = array(new ObjectNormalizer());
        $serializer = new Serializer($normalizers, $encoders);
        $jsonContent=$serializer->serialize($error, 'json');
        return new Response($jsonContent);
    }

    



}
