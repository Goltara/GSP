
#include "ImageSource.hpp"

#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <unistd.h>
#include <stdio.h>

using namespace std;

static void freeImage(IplImage* &image) {
    if (image) {
        cvReleaseImage(&image);
        image = NULL;
    }
}
static bool startsWithAndRemove(string &io, const char* prefix) {
    if (strncmp(io.c_str(), prefix, strlen(prefix))) {
        return false;
    } else {
        io = string(io.c_str() + strlen(prefix));
        return true;
    }
}


ImageSource::ImageSource() : currentImage(NULL), imageIndex(-1), gray(true), pixelStep(1), autoSkip(0) {
}

void ImageSource::skip() {
    switch (mode) {
        case 0:
            imageIndex++;
            break;
        case 1:
            imageIndex++;
            if (!cvGrabFrame(video)) mode = -3;
            emitNamedEvent("end", currentImage);
            break;
    }
}
void ImageSource::input() {
    for (int ii = 0; ii < autoSkip; ii++) skip();
    IplImage *grabbed = NULL;
    switch (mode) {
    case 0: {
        freeImage(currentImage);
        char buf[256];
        snprintf(buf, 255, url.c_str(), imageIndex);
        if (gray) {
            currentImage = cvLoadImage(buf, 0);
        } else {
            currentImage = cvLoadImage(buf);
        }
        if (!currentImage) {
            mode = -2;
            fprintf(stderr, "Could not grab image '%s'\n", buf);
            emitNamedEvent("end", currentImage);
            return;
        }
        fprintf(stderr, "grabbed image '%s'\n", buf);
    }
        break;
    case 1: {
        if (pixelStep>1 || gray) freeImage(currentImage); // we had a temporary image (non grabber), we free it
        grabbed = cvQueryFrame(video); // we never free a cvQueryFrame'd image
        if (!grabbed) {
            mode = -2;
            emitNamedEvent("end", currentImage);
            return;
        }
        currentImage = grabbed;
        // TODO handle grabbing non RGB frames
        if (gray) {
            IplImage* tmp = currentImage;
            currentImage = cvCreateImage(cvSize(tmp->width, tmp->height),IPL_DEPTH_8U, 1);
            cvCvtColor(tmp, currentImage, CV_BGR2GRAY);
        } else {
            // nothing to do (already in color)
        }
        fprintf(stderr, "grabbed video frame %d\n", imageIndex);
        break;
    }
    }
    if (pixelStep > 1) {
        IplImage* tmp = currentImage;
        currentImage = cvCreateImage(cvSize(tmp->width / pixelStep, tmp->height / pixelStep), tmp->depth, tmp->nChannels);
        cvResize(tmp, currentImage, 0); // NN interpolation
        if (tmp != grabbed) freeImage(tmp);
    }
    emitNamedEvent("output", currentImage);
    imageIndex++;
}

void ImageSource::setStart(int imageIndex) {
    this->imageIndex = imageIndex;
}

void ImageSource::setAutoSkip(int autoSkip) {
    this->autoSkip = autoSkip;
}

void ImageSource::setGray(bool gray) {
    this->gray = gray;
}

void ImageSource::setUrl(char *url) {
    if (this->url != "") {
        throw "'url' already set";
    }
    this->url = url;
}
void ImageSource::setPixelStep(int pixelStep) {
    this->pixelStep = pixelStep;
}

void ImageSource::initModule() {
    if (url == "") {
        throw "'url' is unset";
    }
    mode = -1;
    if (startsWithAndRemove(url, "images:")) {
        mode = 0;
        if (imageIndex == -1) imageIndex = 0; // defaults to 0
    } else if (startsWithAndRemove(url, "video:")) {
        mode = 1;
        video = cvCaptureFromFile(url.c_str());
        if (imageIndex == -1) imageIndex = 0;
        for (int s = 0; s < imageIndex; s++) {
            cvGrabFrame(video);
        }
    } else {
        throw "'url' cannot be interpreted (does it start with images: or video: ?";
    }
}

void ImageSource::stopModule()
{
    freeImage(currentImage);
}


