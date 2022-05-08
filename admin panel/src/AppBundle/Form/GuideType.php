<?php
namespace AppBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormEvent;
use Symfony\Component\Form\FormEvents;
use Ivory\CKEditorBundle\Form\Type\CKEditorType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;

class GuideType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
       $builder
            ->add('title',null,array("label"=>"Guide title"))
            ->add('category',null,array("label"=>"Guide Category"))
            ->add('content', CKEditorType::class, 
                    array(
                        'config_name' => 'user_config'
                    )
                )
                ->add("enabled")
                ->add("comment");
        $builder->addEventListener(FormEvents::PRE_SET_DATA, function (FormEvent $event) {
            $article = $event->getData();
            $form = $event->getForm();
            if ($article and null !== $article->getId()) {
                 $form->add("file",null,array("label"=>"","required"=>false));
                 $form->add('save', 'submit',array("label"=>"SAVE"));

            }else{
                 $form->add("file",null,array("label"=>"","required"=>true));
                 $form->add('save', 'submit',array("label"=>"NEXT"));
            }
        });

    }
    public function getName()
    {
        return 'Guide';
    }
}
?>
